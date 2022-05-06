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

    @Test
    @DisplayName("Should return true when exists a registration was already created")
    public void returnTrueWhenRegistrationExists(){

        String personName = "Mariela Fernandez";
        String email = "email@gmail.com";
        Meetup meetup = createNewMeetup("Womakerscode Dados");
        entityManager.persist(meetup);
        Registration registration = createNewRegistration(personName, email, meetup);
        entityManager.persist(registration);

        boolean exists = repository.existsByEmailAndMeetup(email, meetup);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when a registration doesn't exists")
    public void returnFalseWhenRegistrationDoesntExists(){
        String email = "email@gmail.com";
        Meetup meetup = Meetup.builder().id(101).build();

        boolean exists = repository.existsByEmailAndMeetup(email, meetup);

        assertThat(exists).isFalse();

    }

    @Test
    @DisplayName("Should get a registration by id")
    public void findByIdTest(){

        String personName = "Mariela Fernandez";
        String email = "email@gmail.com";
        Meetup meetup = createNewMeetup("Womakerscode Dados");
        entityManager.persist(meetup);
        Registration registration = createNewRegistration(personName, email,meetup);
        entityManager.persist(registration);

        Optional<Registration> foundRegistration = repository.findById(registration.getId());

        assertThat(foundRegistration.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Should save registration")
    public void saveRegistrationTest(){
        String personName = "Mariela Fernandez";
        String email = "email@gmail.com";
        Meetup meetup = createNewMeetup("Womakerscode Dados");
        entityManager.persist(meetup);
        Registration registration = createNewRegistration(personName, email,meetup);

        //simular do que persistiu
        Registration savedRegistration = repository.save(registration);

        assertThat(savedRegistration.getId()).isNotNull();

    }

    @Test
    @DisplayName("Should delete a registration from the BD")
    public void deleteRegistrationTest(){
        String personName = "Mariela Fernandez";
        String email = "email@gmail.com";
        Meetup meetup = createNewMeetup("Womakerscode Dados");
        entityManager.persist(meetup);
        Registration registration = createNewRegistration(personName, email, meetup);
        entityManager.persist(registration);

        Registration foundRegistration = entityManager.find(Registration.class, registration.getId());

        repository.delete(foundRegistration);

        Registration deleteRegistration = entityManager.find(Registration.class, registration.getId());

        assertThat(deleteRegistration).isNull();

    }

    public Registration createNewRegistration(String personName, String email, Meetup meetup) {

        return Registration.builder()
                .personName(personName)
                .email(email)
                .dateOfRegistration("10/10/2021")
                .registered(true)
                .meetup(meetup)
                .build();
    }

    public Meetup createNewMeetup(String event) {

        return Meetup.builder()
                .event(event)
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();
    }

    private Registration createValidRegistration() {
        Meetup meetup = createValidMeetup();

        return Registration.builder()
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
