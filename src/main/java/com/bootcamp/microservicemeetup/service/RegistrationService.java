package com.bootcamp.microservicemeetup.service;

import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.dto.RegistrationDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RegistrationService {

    Registration save(Registration any);

    Optional<Registration> getRegistrationById(Integer id);

    void delete(Registration registration);

    Registration update(Registration registration);

    Page<Registration> find(Registration filter, Pageable pageRequest);

    List<RegistrationDTO> getAll();

    Page<Registration> getRegistrationsByMeetup(Meetup meetup, Pageable pageRequest);

}
