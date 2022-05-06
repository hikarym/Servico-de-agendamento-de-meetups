package com.bootcamp.microservicemeetup.repository;

import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.dto.RegistrationDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class RegistrationRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    RegistrationRepository repository;

//    @Test
//    @DisplayName("Should return true when exists a registration was already created")
//    public void returnTrueWhenRegistrationExists(){
//        String email = "email@gmail.com";
//        Meetup meetup = Meetup.builder().id(11).build();
//        Registration registration = createNewRegistration(email);
//        entityManager.persist(registration);
//
//        boolean exists = repository.existsByEmailAndMeetup(email, meetup);
//
//        assertThat(exists).isTrue();
//    }

    @Test
    @DisplayName("Should return false when a registration doesn't exists")
    public void returnFalseWhenRegistrationDoesntExists(){
        String email = "email@gmail.com";
        Meetup meetup = Meetup.builder().id(101).build();

        boolean exists = repository.existsByEmailAndMeetup(email, meetup);

        assertThat(exists).isFalse();

    }

//    @Test
//    @DisplayName("Should get a registration by id")
//    public void findByIdTest(){
//        Registration registration = createValidRegistration();
//        entityManager.persist(registration);
//
//        Optional<Registration> foundRegistration = repository.findById(registration.getId());
//
//        assertThat(foundRegistration.isPresent()).isTrue();
//
//    }
//
//    @Test
//    @DisplayName("Should save registration")
//    public void saveRegistrationTest(){
//        Registration registration = createNewRegistration("email@gmail.com");
//
//        //simular do que persistiu
//        Registration savedRegistration = repository.save(registration);
//
//        assertThat(savedRegistration.getId()).isNotNull();
//
//    }
//
//    @DisplayName("Should delete a registration from the BD")
//    public void deleteRegistrationTest(){
//        Registration registration_Class_attribute = createNewRegistration("323");
//        entityManager.persist(registration_Class_attribute);
//
//        Registration foundRegistration = entityManager.find(Registration.class, registration_Class_attribute.getId());
//
//        repository.delete(foundRegistration);
//
//        Registration deleteRegistration = entityManager.find(Registration.class, registration_Class_attribute.getId());
//
//        assertThat(deleteRegistration).isNull();
//
//    }

    public Registration createNewRegistration(String email) {
        Meetup meetup = Meetup.builder()
                .id(11)
                .event("Womakerscode Dados")
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();

        return Registration.builder()
                .id(101)
                .personName("Mariela Fernandez")
                .email(email)
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
                .id(11)
                .personName("Mariela Fernandez")
                .email("email@gmail.com")
                .dateOfRegistration("10/10/2021")
                .registered(true)
                .meetup(meetupDTO)
                .build();
    }

    private Meetup createValidMeetup() {
        return Meetup.builder()
                .id(101)
                .event("Womakerscode Dados")
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();
    }

    private MeetupDTO createValidMeetupDTO() {
        return MeetupDTO.builder()
                .id(101)
                .event("Womakerscode Dados")
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();
    }
}
