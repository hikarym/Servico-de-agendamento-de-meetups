package com.bootcamp.microservicemeetup.service;

import com.bootcamp.microservicemeetup.exception.BusinessException;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.repository.MeetupRepository;
import com.bootcamp.microservicemeetup.service.impl.MeetupServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MeetupServiceTest {

    MeetupService meetupService;

    @MockBean
    MeetupRepository meetupRepository;


    @BeforeEach
    public void setUp() {

        this.meetupService = new MeetupServiceImpl(meetupRepository);
    }

    @Test
    @DisplayName("Should save an meetup")
    public void saveMeetupTest() {

        Meetup savingMeetup = Meetup.builder()
                .event("Womakerscode Dados")
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();


        Meetup savedMeetup = Meetup.builder()
                .id(11)
                .event("Womakerscode Dados")
                .description("descricao")
                .organizer("organizadora")
                .meetupDate("10/10/2021")
                .address("sao paulo")
                .build();

        Mockito.when(meetupRepository.save(savingMeetup)).thenReturn(savedMeetup);

        Meetup meetup = meetupService.save(savingMeetup);

        assertThat(meetup.getId()).isEqualTo(savedMeetup.getId());
        assertThat(meetup.getEvent()).isEqualTo(savedMeetup.getEvent());
        assertThat(meetup.getDescription()).isEqualTo(savedMeetup.getDescription());
        assertThat(meetup.getOrganizer()).isEqualTo(savedMeetup.getOrganizer());
        assertThat(meetup.getMeetupDate()).isEqualTo(savedMeetup.getMeetupDate());
        assertThat(meetup.getAddress()).isEqualTo(savedMeetup.getAddress());
    }

    @Test
    @DisplayName("Should get an meetup by meetupId")
    public void getMeetupByIdTest() {
        Integer meetupId = 11;
        Meetup meetup = createValidMeetup();
        meetup.setId(meetupId);
        Mockito.when(meetupRepository.findById(meetupId)).thenReturn(Optional.of(meetup));


        Optional<Meetup> foundmeetup = meetupService.getMeetupById(meetupId);

        assertThat(foundmeetup.isPresent()).isTrue();
        assertThat(foundmeetup.get().getId()).isEqualTo(meetupId);
        assertThat(foundmeetup.get().getEvent()).isEqualTo(meetup.getEvent());
        assertThat(foundmeetup.get().getDescription()).isEqualTo(meetup.getDescription());
        assertThat(foundmeetup.get().getOrganizer()).isEqualTo(meetup.getOrganizer());
        assertThat(foundmeetup.get().getMeetupDate()).isEqualTo(meetup.getMeetupDate());
        assertThat(foundmeetup.get().getAddress()).isEqualTo(meetup.getAddress());

    }

    @Test
    @DisplayName("Should return empty when get an meetup by Id when doesn't exists.")
    public void MeetupNotFoundByIdTest() {
        Integer meetupId = 11;

        Mockito.when(meetupRepository.findById(meetupId)).thenReturn(Optional.empty());

        Optional<Meetup> meetup = meetupService.getMeetupById(meetupId);

        assertThat(meetup.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Should delete an meetup")
    public void deleteMeetupTest() {
        Meetup meetup = Meetup.builder().id(11).build();

        assertDoesNotThrow(() -> meetupService.delete(meetup));

        Mockito.verify(meetupRepository, Mockito.times(1)).delete(meetup);
    }

    @Test
    @DisplayName("Should throw error when try to delete an meetup no existent")
    public void deleteInvalidMeetupTest() {
        Meetup meetup = new Meetup();

        assertThrows(IllegalArgumentException.class, () -> meetupService.delete(meetup) );

        Mockito.verify(meetupRepository, Mockito.never()).delete(meetup);
    }

    @Test
    @DisplayName("Should update an meetup")
    public void updateMeetupTest() {

        Integer meetupId = 11;
        Meetup updatingMeetup = Meetup.builder().id(meetupId).build();

        Meetup updatedMeetup = createValidMeetup();
        updatedMeetup.setId(meetupId);

        Mockito.when(meetupRepository.save(updatingMeetup)).thenReturn(updatedMeetup);

        Meetup meetup =  meetupService.update(updatingMeetup);

        assertThat(meetup.getId()).isEqualTo(meetupId);
        assertThat(meetup.getEvent()).isEqualTo(meetup.getEvent());
        assertThat(meetup.getDescription()).isEqualTo(meetup.getDescription());
        assertThat(meetup.getOrganizer()).isEqualTo(meetup.getOrganizer());
        assertThat(meetup.getMeetupDate()).isEqualTo(meetup.getMeetupDate());
        assertThat(meetup.getAddress()).isEqualTo(meetup.getAddress());

    }

    @Test
    @DisplayName("Should filter meetups must by properties")
    public void findMeetupTest() {

        // scenario
        Meetup meetup = createValidMeetup();
        PageRequest pageRequest = PageRequest.of(0,10);

        List<Meetup> listMeetups = Arrays.asList(meetup);
        Page<Meetup> page  = new PageImpl<>(Arrays.asList(meetup), PageRequest.of(0,10), 1);

        // Example class é do spring data
        Mockito.when(meetupRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        // execute

        // o objeto serve pra comparar com o que esta na base de dados
        Page<Meetup> result =  meetupService.find(meetup, pageRequest);

        // verify
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(listMeetups);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
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

}
