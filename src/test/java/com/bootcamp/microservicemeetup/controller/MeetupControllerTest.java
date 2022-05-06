package com.bootcamp.microservicemeetup.controller;

import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.resource.MeetupController;
import com.bootcamp.microservicemeetup.exception.BusinessException;
import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.service.MeetupService;
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {MeetupController.class})
// Annotation that can be applied to a test class to enable and configure auto-configuration of MockMvc
@AutoConfigureMockMvc
public class MeetupControllerTest {

    static final String MEETUP_API = "/api/meetup";

    // Fazer injeção de dependencia
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private MeetupService meetupService;

    @MockBean
    private RegistrationService registrationService;

    @Test
    @DisplayName("Should register on a meetup")
    public void createMeetupTest() throws Exception {

        // quando enviar uma requisicao para esse meetup
        // precisa ser encontrado um valor que tem esse usuario
        MeetupDTO dto = MeetupDTO.builder()
                .event("Womakerscode Dados")
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();

        String json = new ObjectMapper().writeValueAsString(dto);


        Meetup meetup = Meetup.builder()
                .id(11)
                .event("Womakerscode Dados")
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();

        BDDMockito.given(meetupService.save(Mockito.any(Meetup.class))).willReturn(meetup);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(MEETUP_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // Aqui o que retorna é o id do registro no meetup
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("11"));
    }

    @Test
    @DisplayName("Should get meetup information")
    public void getMeetupTest() throws Exception {
        Integer meetupId = 11;
        String event = "Womakerscode Dados";
        Meetup meetup = createNewMeetup(meetupId, event);

        BDDMockito.given(meetupService.getMeetupById(meetupId)).willReturn(Optional.of(meetup));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(MEETUP_API.concat("/" + meetupId))
                .accept(MediaType.APPLICATION_JSON);

        // verificacao, assert
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(meetupId))
                .andExpect(jsonPath("event").value(createNewMeetup(meetupId, event).getEvent()))
                .andExpect(jsonPath("description").value(createNewMeetup(meetupId, event).getDescription()))
                .andExpect(jsonPath("organizer").value(createNewMeetup(meetupId, event).getOrganizer()))
                .andExpect(jsonPath("meetupDate").value(createNewMeetup(meetupId, event).getMeetupDate()))
                .andExpect(jsonPath("address").value(createNewMeetup(meetupId, event).getAddress()));

    }

    @Test
    @DisplayName("Should return NOT FOUND when meetup doesn't exist")
    public void meetupNotFoundTest() throws Exception {

        BDDMockito.given(meetupService.getMeetupById(anyInt())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(MEETUP_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should delete the meetup")
    public void deleteMeetupTest() throws Exception {

        BDDMockito.given(meetupService
                        .getMeetupById(anyInt()))
                .willReturn(Optional.of(Meetup.builder().id(11).build()));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(MEETUP_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Should return resource not found when doesn't exist meetup")
    public void deleteNonExistentMeetupTest() throws Exception {

        BDDMockito.given(meetupService
                        .getMeetupById(anyInt()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(MEETUP_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should update when meetup info")
    public void updateMeetupTest() throws Exception {
        Integer meetupId = 11;
        String event = "Womakerscode Dados";
        Meetup meetup = createNewMeetup(meetupId, event);
        String json = new ObjectMapper().writeValueAsString(meetup);

        Meetup updatingMeetup = Meetup.builder()
                .id(meetupId)
                .event(event)
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("25/11/2021")
                .address("Brasilia")
                .build();


        BDDMockito.given(meetupService.getMeetupById(meetupId))
                .willReturn(Optional.of(updatingMeetup));

        Meetup updateMeetup = Meetup.builder()
                .id(meetupId)
                .event(event)
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();


        BDDMockito.given(meetupService
                        .update(updatingMeetup))
                .willReturn(updateMeetup);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(MEETUP_API.concat("/" + meetupId))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // verificacao, assert
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(meetupId))
                .andExpect(jsonPath("event").value(createNewMeetup(meetupId, event).getEvent()))
                .andExpect(jsonPath("description").value(createNewMeetup(meetupId, event).getDescription()))
                .andExpect(jsonPath("organizer").value(createNewMeetup(meetupId, event).getOrganizer()))
                .andExpect(jsonPath("meetupDate").value(createNewMeetup(meetupId, event).getMeetupDate()))
                .andExpect(jsonPath("address").value(createNewMeetup(meetupId, event).getAddress()));

    }

    @Test
    @DisplayName("Should return 404 when try to update a meetup no existent ")
    public void updateNonExistentMeetupTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(createValidMeetup());

        BDDMockito.given(meetupService.getMeetupById(anyInt()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(MEETUP_API.concat("/" + 1))
                .contentType(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // verificacao, assert
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter  meetup ")
    public void findMeetupTest() throws Exception {
        Integer id = 11;

        Meetup meetup = Meetup.builder()
                .id(id)
                .event(createValidMeetup().getEvent())
                .description(createValidMeetup().getDescription())
                .organizer(createValidMeetup().getOrganizer())
                .meetupDate(createValidMeetup().getMeetupDate())
                .address(createValidMeetup().getAddress())
                .build();


        BDDMockito.given(meetupService.find(Mockito.any(Meetup.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(meetup), PageRequest.of(0, 100), 1));

        String queryString = String.format("?event=%s&organizer=%s&page=0&size=100",
                meetup.getEvent(), meetup.getOrganizer());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(MEETUP_API.concat("/find" + queryString))
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

    public MeetupDTO createNewMeetupDTO(Integer meetupId, String event) {
        return MeetupDTO.builder()
                .id(meetupId)
                .event(event)
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();
    }

    public Meetup createNewMeetup(Integer meetupId, String event) {
        return Meetup.builder()
                .id(meetupId)
                .event(event)
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();
    }
}
