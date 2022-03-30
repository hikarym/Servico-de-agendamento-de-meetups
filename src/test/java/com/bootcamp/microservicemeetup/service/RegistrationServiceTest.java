package com.bootcamp.microservicemeetup.service;

import com.bootcamp.microservicemeetup.exception.BusinessException;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.repository.RegistrationRepository;
import com.bootcamp.microservicemeetup.service.impl.RegistrationServiceImpl;
import org.assertj.core.api.Assertions;//.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

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
        Assertions.assertThat(savedRegistration.getId()).isEqualTo(101);
        Assertions.assertThat(savedRegistration.getName()).isEqualTo("Mariela Fernandez");
        Assertions.assertThat(savedRegistration.getDateOfRegistration()).isEqualTo(LocalDate.now());
        Assertions.assertThat(savedRegistration.getRegistration()).isEqualTo("001");

    }

    private Registration createValidRegistration() {
        return Registration.builder()
                .id(101)
                .name("Mariela Fernandez")
                .dateOfRegistration(LocalDate.now())
                .registration("001")
                .build();
    }

    @Test
    @DisplayName("Should throw Business error when to try saving a new registration with a registration duplicated")
    public void shouldNotSaveAsRegistrationDuplicated() {

        Registration registration = createValidRegistration();
        Mockito.when(repository.existsByRegistration(Mockito.any())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> registrationService.save(registration));
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Registration already created");

        Mockito.verify(repository, Mockito.never()).save(registration);
    }

    @Test
    @DisplayName("Should get a registration by Id")
    public void getByRegistrationIdTest() {

        // cenario
        Integer id = 11;
        Registration registration= createValidRegistration();
        registration.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(registration));

        // execucao
        Optional<Registration> foundRegistration = registrationService.getRegistrationById(id);

        //assert
        Assertions.assertThat(foundRegistration.isPresent()).isTrue();
        Assertions.assertThat(foundRegistration.get().getId()).isEqualTo(id);
        Assertions.assertThat(foundRegistration.get().getName()).isEqualTo(registration.getName());
        Assertions.assertThat(foundRegistration.get().getDateOfRegistration()).isEqualTo(registration.getDateOfRegistration());
        Assertions.assertThat(foundRegistration.get().getRegistration()).isEqualTo(registration.getRegistration());
    }
}
