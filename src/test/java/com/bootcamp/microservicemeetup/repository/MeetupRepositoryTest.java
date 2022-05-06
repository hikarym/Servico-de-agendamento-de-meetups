package com.bootcamp.microservicemeetup.repository;

import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
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
public class MeetupRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    MeetupRepository repository;

    @Test
    @DisplayName("Should return true when exists a meetup was already created")
    public void returnTrueWhenMeetupExists(){
        Meetup meetup = createNewMeetup("Womakerscode Dados");
        entityManager.persist(meetup);

        boolean exists = repository.existsById(meetup.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when a meetup doesn't exist")
    public void returnFalseWhenMeetupDoesntExists(){
        String email = "email@gmail.com";
        Meetup meetup = Meetup.builder().id(101).build();

        boolean exists = repository.existsById(meetup.getId());

        assertThat(exists).isFalse();

    }

    @Test
    @DisplayName("Should get a meetup by id")
    public void findByIdTest(){

        Meetup meetup = createNewMeetup("Womakerscode Dados");
        entityManager.persist(meetup);

        Optional<Meetup> foundMeetup = repository.findById(meetup.getId());

        assertThat(foundMeetup.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Should save meetup")
    public void saveMeetupTest(){

        Meetup meetup = createNewMeetup("Womakerscode Dados");
        entityManager.persist(meetup);

        //simular do que persistiu
        Meetup savedMeetup = repository.save(meetup);

        assertThat(savedMeetup.getId()).isNotNull();

    }

    @Test
    @DisplayName("Should delete a meetup from the BD")
    public void deleteMeetupTest(){

        Meetup meetup = createNewMeetup("Womakerscode Dados");
        entityManager.persist(meetup);

        Meetup foundMeetup = entityManager.find(Meetup.class, meetup.getId());

        repository.delete(foundMeetup);

        Meetup deleteMeetup = entityManager.find(Meetup.class, meetup.getId());

        assertThat(deleteMeetup).isNull();

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
}
