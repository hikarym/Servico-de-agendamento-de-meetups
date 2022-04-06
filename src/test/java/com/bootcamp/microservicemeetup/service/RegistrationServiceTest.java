package com.bootcamp.microservicemeetup.service;

import com.bootcamp.microservicemeetup.exception.BusinessException;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.repository.RegistrationRepository;
import com.bootcamp.microservicemeetup.service.impl.RegistrationServiceImpl;
import org.assertj.core.api.Assertions;
import org.springframework.data.domain.Example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RegistrationServiceTest {

    RegistrationService registrationService;

    @MockBean
    RegistrationRepository repository;

    @BeforeEach
    public void setUp() {
        // dependencia do service e dar um new na mesma
        this.registrationService = new RegistrationServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save a registration")
    public void saveStudent() {

        // cenario
        Registration registration= createValidRegistration();

        // execucao
        Mockito.when(repository.existsByRegistration(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(registration)).thenReturn(createValidRegistration());

        Registration savedRegistration = registrationService.save(registration);
        // assert
        assertThat(savedRegistration.getId()).isEqualTo(101);
        assertThat(savedRegistration.getName()).isEqualTo("Mariela Fernandez");
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo("01/04/2022");
        assertThat(savedRegistration.getRegistration()).isEqualTo("001");

    }

    @Test
    @DisplayName("Should throw Business error when to try saving a new registration with a registration duplicated")
    public void shouldNotSaveAsRegistrationDuplicated() {

        Registration registration = createValidRegistration();
        Mockito.when(repository.existsByRegistration(Mockito.any())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> registrationService.save(registration));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Registration already created");

        // It means that the method save(registration) must not be called
        Mockito.verify(repository, Mockito.never()).save(registration);
    }

    @Test
    @DisplayName("Should get a registration by Id")
    public void getByRegistrationIdTest() {

        // scenario
        Integer id = 11;
        Registration registration= createValidRegistration();
        registration.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(registration));

        // execution
        Optional<Registration> foundRegistration = registrationService.getRegistrationById(id);

        //assert
        assertThat(foundRegistration.isPresent()).isTrue();
        assertThat(foundRegistration.get().getId()).isEqualTo(id);
        assertThat(foundRegistration.get().getName()).isEqualTo(registration.getName());
        assertThat(foundRegistration.get().getDateOfRegistration()).isEqualTo(registration.getDateOfRegistration());
        assertThat(foundRegistration.get().getRegistration()).isEqualTo(registration.getRegistration());
    }

    @Test
    @DisplayName("Should return empty when get a registration by id when it doesn't exist")
    public void registrationNotFoundByIdTest() {

        // cenario. simulando o cenário de erro
        Integer id = 11;
        // caso de retornar vazio
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        // execucao
        Optional<Registration> registration = registrationService.getRegistrationById(id);

        //assert
        assertThat(registration.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should delete a student")
    public void deleteRegistrationTest() {

        // cenario. simulando o cenário de erro
        Registration registration = Registration.builder().id(11).build();

        // para não estourar erro
        assertDoesNotThrow(() -> registrationService.delete(registration));

        // caso de retornar vazio
        Mockito.verify(repository, Mockito.times(1)).delete(registration);

        // verify whether registration was deleted
    }

    @Test
    @DisplayName("Should throw error when trying to delete an non-existing registration ")
    public void deleteInvalidRegistrationTest() {

        // scenario. simulando o cenário de erro
        Registration registration = new Registration();

        assertThrows(IllegalArgumentException.class, () -> registrationService.delete(registration));

        //
        Mockito.verify(repository, Mockito.never()).delete(registration);
    }

    @Test
    @DisplayName("Should update a registration")
    public void updateRegistrationTest() {

        // scenario. simulando o cenário de erro
        Integer id = 11;
        Registration updatingRegistration = Registration.builder().id(11).build();

        // execution
        Registration updatedRegistration = createValidRegistration();
        updatedRegistration.setId(id);

        //
        Mockito.when(repository.save(updatingRegistration)).thenReturn(updatedRegistration);
        Registration registration = registrationService.update(updatingRegistration);

        // assert
        // assertThat(registration).isEqualTo(updatedRegistration);
        assertThat(registration.getId()).isEqualTo(updatedRegistration.getId());
        assertThat(registration.getName()).isEqualTo(updatedRegistration.getName());
        assertThat(registration.getDateOfRegistration()).isEqualTo(updatedRegistration.getDateOfRegistration());
        assertThat(registration.getRegistration()).isEqualTo(updatedRegistration.getRegistration());

    }

    @Test
    @DisplayName("Should filter registrations by properties")
    public void findRegistrationTest() {

        // scenario: simulando o cenário de erro
        Registration registration = createValidRegistration();
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Registration> listRegistrations = Arrays.asList(registration);
        Page<Registration> page = new PageImpl<Registration>(Arrays.asList(registration), PageRequest.of(0, 10), 1);

        // caso de retornar vazio
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);

        Page<Registration> result = registrationService.find(registration, pageRequest);

        // assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(listRegistrations);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should get a registration model by registration attribute")
    public void getRegistrationByRegistration() {

        // scenario: simulando o cenário de erro
        String registrationAttribute = "1234";

        // caso de retornar vazio
        Mockito.when(repository.findByRegistration(registrationAttribute))
                .thenReturn(Optional.of(Registration.builder().id(11).registration(registrationAttribute).build()));

        Optional<Registration> registration = registrationService.getRegistrationByRegistrationAttribute(registrationAttribute);

        // assert
        assertThat(registration.isPresent()).isTrue();
        assertThat(registration.get().getId()).isEqualTo(11);
        assertThat(registration.get().getRegistration()).isEqualTo(registrationAttribute);
    }

    private Registration createValidRegistration() {
        return Registration.builder()
                .id(101)
                .name("Mariela Fernandez")
                .dateOfRegistration("01/04/2022")
                .registration("001")
                .build();
    }
}
