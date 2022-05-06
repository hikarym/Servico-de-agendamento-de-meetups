package com.bootcamp.microservicemeetup.controller;

import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.resource.RegistrationController;
import com.bootcamp.microservicemeetup.exception.BusinessException;
import com.bootcamp.microservicemeetup.controller.dto.RegistrationDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.service.MeetupService;
import com.bootcamp.microservicemeetup.service.RegistrationService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {RegistrationController.class})
// Annotation that can be applied to a test class to enable and configure auto-configuration of MockMvc
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    static  String REGISTRATION_API = "/api/registration";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RegistrationService registrationService;

    @MockBean
    MeetupService meetupService;

    @Test
    @DisplayName("Should create a registration with success")
    public void createRegistrationTest() throws Exception {

        // scenario
        Meetup meetup = createValidMeetup();
        RegistrationDTO registrationDTOBuilder = createNewRegistration();
        Registration savedRegistration = Registration.builder()
                .id(101)
                .personName("Mariela Fernandez")
                .email("email@gmail.com")
                .dateOfRegistration("10/10/2021")
                .registered(true)
                .meetup(meetup)
                .build();

        //  BDD: simula camada do usuário
        BDDMockito.given(registrationService.save(any(Registration.class))).willReturn(savedRegistration);

        // Define como retornar
        String json = new ObjectMapper().writeValueAsString(registrationDTOBuilder);

        BDDMockito.given(meetupService.getMeetupById(11)).
                willReturn(Optional.of(meetup));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verificacao, assert
        mockMvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(101))
                .andExpect(jsonPath("personName").value(registrationDTOBuilder.getPersonName()))
                .andExpect(jsonPath("email").value(registrationDTOBuilder.getEmail()))
                .andExpect(jsonPath("dateOfRegistration").value(registrationDTOBuilder.getDateOfRegistration()))
                .andExpect(jsonPath("registered").value(registrationDTOBuilder.getRegistered()))
                .andExpect(jsonPath("meetup").value(registrationDTOBuilder.getMeetup()));

    }

    @Test
    @DisplayName("Should throw an exception when not have data enough")
    public void createInvalidRegistrationTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new RegistrationDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Should throw an exception when try to create a new registration with another registration created")
    public void createRegistrationDuplicated() throws Exception {

        // scenario
        Meetup meetup = createValidMeetup();
        RegistrationDTO dto = createNewRegistration();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(meetupService.getMeetupById(11)).
                willReturn(Optional.of(meetup));

        //  BDD: simula camada do usuário
        BDDMockito.given(registrationService.save(any(Registration.class)))
                .willThrow(new BusinessException("Registration already created"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Registration already created"));
    }


    @Test
    @DisplayName("Should get registration information")
    public void getRegistrationTest() throws Exception {
        Integer id = 11;
        Meetup meetup = createValidMeetup();
        Registration registration = Registration.builder()
                .id(id)
                .personName(createNewRegistration().getPersonName())
                .email(createNewRegistration().getEmail())
                .dateOfRegistration(createNewRegistration().getDateOfRegistration())
                .registered(createNewRegistration().getRegistered())
                .meetup(Meetup.builder()
                        .id(createNewRegistration().getMeetup().getId())
                        .event(createNewRegistration().getMeetup().getEvent())
                        .description(createNewRegistration().getMeetup().getDescription())
                        .organizer(createNewRegistration().getMeetup().getOrganizer())
                        .meetupDate(createNewRegistration().getMeetup().getMeetupDate())
                        .address(createNewRegistration().getMeetup().getAddress())
                        .build()
                )
                .build();

        BDDMockito.given(registrationService.getRegistrationById(id)).willReturn(Optional.of(registration));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // verificacao, assert
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("personName").value(createNewRegistration().getPersonName()))
                .andExpect(jsonPath("email").value(createNewRegistration().getEmail()))
                .andExpect(jsonPath("dateOfRegistration").value(createNewRegistration().getDateOfRegistration()))
                .andExpect(jsonPath("registered").value(createNewRegistration().getRegistered()))
                .andExpect(jsonPath("meetup").value(createNewRegistration().getMeetup()));

    }

    @Test
    @DisplayName("Should return NOT FOUND when registration doesn't exist")
    public void registrationNotFoundTest() throws Exception {

        BDDMockito.given(registrationService.getRegistrationById(anyInt())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should delete the registration")
    public void deleteRegistrationTest() throws Exception {

        BDDMockito.given(registrationService
                .getRegistrationById(anyInt()))
                .willReturn(Optional.of(Registration.builder().id(11).build()));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(REGISTRATION_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Should return resource not found when doesnt exist registration")
    public void deleteNonExistentRegistrationTest() throws Exception {

        BDDMockito.given(registrationService
                        .getRegistrationById(anyInt()))
                        .willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should update when registration info")
    public void updateRegistrationTest() throws Exception {
        Integer registrationId = 11;
        String json = new ObjectMapper().writeValueAsString(createNewRegistration());
        Integer meetupId = 11;
        Meetup meetup = Meetup.builder()
                .id(meetupId)
                .event("Womakerscode Dados")
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();

        BDDMockito.given(meetupService.getMeetupById(meetupId)).
                willReturn(Optional.of(meetup));

        Registration updatingRegistration = Registration.builder()
                .id(registrationId)
                .personName("Juana Fernandez")
                .email("emailjuana@gmail.com")
                .dateOfRegistration("10/10/2021")
                .registered(true)
                .meetup(meetup)
                .build();


        BDDMockito.given(registrationService.getRegistrationById(registrationId))
                .willReturn(Optional.of(updatingRegistration));

        Registration updateRegistration = Registration.builder()
                .id(registrationId)
                .personName("Mariela Fernandez")
                .email("email@gmail.com")
                .dateOfRegistration("10/10/2021")
                .registered(true)
                .meetup(meetup)
                .build();


        BDDMockito.given(registrationService
                .update(updatingRegistration))
                .willReturn(updateRegistration);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(REGISTRATION_API.concat("/" + registrationId))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // verificacao, assert
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(registrationId))
                .andExpect(jsonPath("personName").value(createNewRegistration().getPersonName()))
                .andExpect(jsonPath("email").value(createNewRegistration().getEmail()))
                .andExpect(jsonPath("dateOfRegistration").value(createNewRegistration().getDateOfRegistration()))
                .andExpect(jsonPath("registered").value(createNewRegistration().getRegistered()))
                .andExpect(jsonPath("meetup").value(createNewRegistration().getMeetup()));



    }

    @Test
    @DisplayName("Should return 404 when try to update a registration no existent ")
    public void updateNonExistentRegistrationTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(createNewRegistration());

        BDDMockito.given(registrationService.getRegistrationById(anyInt()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/" + 1))
                .contentType(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // verificacao, assert
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter  registration ")
    public void findRegistrationTest() throws Exception {
        Integer id = 11;

        Registration registration = Registration.builder()
                .id(id)
                .personName(createNewRegistration().getPersonName())
                .email(createNewRegistration().getEmail())
                .dateOfRegistration(createNewRegistration().getDateOfRegistration())
                .registered(createNewRegistration().getRegistered())
                .meetup(Meetup.builder()
                        .id(createNewRegistration().getMeetup().getId())
                        .event(createNewRegistration().getMeetup().getEvent())
                        .description(createNewRegistration().getMeetup().getDescription())
                        .organizer(createNewRegistration().getMeetup().getOrganizer())
                        .meetupDate(createNewRegistration().getMeetup().getMeetupDate())
                        .address(createNewRegistration().getMeetup().getAddress())
                        .build())
                .build();


        BDDMockito.given(registrationService.find(Mockito.any(Registration.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(registration), PageRequest.of(0, 100), 1));

        String queryString = String.format("?personName=%s&dateOfRegistration=%s&page=0&size=100",
                registration.getPersonName(), registration.getDateOfRegistration());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/find" + queryString))
                .accept(MediaType.APPLICATION_JSON);

        // verificacao, assert
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }


    private RegistrationDTO createNewRegistration() {
        MeetupDTO meetup = createValidMeetupDTO();

        return RegistrationDTO.builder()
                .id(101)
                .personName("Mariela Fernandez")
                .email("email@gmail.com")
                .dateOfRegistration("10/10/2021")
                .registered(true)
                .meetup(meetup)
                .build();
    }

    private Registration createValidRegistration() {
        Meetup meetup = createValidMeetup();

        return Registration.builder()
                .id(101)
                .personName("Mariela Fernandez")
                .email("email@gmail.com")
                .dateOfRegistration("10/10/2021")
                .registered(true)
                .meetup(meetup)
                .build();
    }

    private RegistrationDTO createValidRegistrationDTO() {
        MeetupDTO meetupDTO = createValidMeetupDTO();

        return RegistrationDTO.builder()
                .id(101)
                .personName("Mariela Fernandez")
                .email("email@gmail.com")
                .dateOfRegistration("10/10/2021")
                .registered(true)
                .meetup(meetupDTO)
                .build();
    }

    private Meetup createValidMeetup() {
        return Meetup.builder()
                .id(11)
                .event("Womakerscode Dados")
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();
    }

    private MeetupDTO createValidMeetupDTO() {
        return MeetupDTO.builder()
                .id(11)
                .event("Womakerscode Dados")
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();
    }


}
