package com.bootcamp.microservicemeetup.service;

import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MeetupService {

    Meetup save(Meetup meetup);

    Optional<Meetup> getMeetupById(Integer id);

     Meetup update(Meetup meetup);

    Page<Meetup> find(Meetup filter, Pageable pageable);

    List<MeetupDTO> getAll();

    void delete(Meetup meetup);

}
