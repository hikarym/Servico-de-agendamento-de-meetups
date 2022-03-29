package com.bootcamp.microservicemeetup.service;

import com.bootcamp.microservicemeetup.model.entity.Registration;
import org.junit.jupiter.api.Assert.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RegistrationServiceTest {

    @BeforeEach
    public void setUp() {
        // dependencia do service e dar um new na mesma

    }

    @Test
    @DisplayName("Should save a registration")
    public void saveStudent() {

        // cenario
        Registration registration= createValidRegistration();

        // execucao
        Mockito.when(repository.existsByRegistration(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(registration)).thenReturn(createdValidRegistration());

        Registration savedRegistration = registrationService.save(registration);
        // assert
        assertThat(savedRegistration.getId()).isEqualTo(101);
        assertThat(savedRegistration.getName()).isEqualTo("Mariela Fernandez");
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo(LocalDate.now());
        assertThat(savedRegistration.getRegistration()).isEqualTo("001");

    }

    private Registration createValidRegistration() {
        return Registration.builder()
                .id(101)
                .name("Mariela Fernandez")
                .dateOfRegistration(LocalDate.now())
                .registration("001")
                .build();
    }
}
