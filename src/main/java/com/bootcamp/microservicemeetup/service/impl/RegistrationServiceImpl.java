package com.bootcamp.microservicemeetup.service.impl;

import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.dto.RegistrationDTO;
import com.bootcamp.microservicemeetup.exception.BusinessException;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.repository.RegistrationRepository;
import com.bootcamp.microservicemeetup.service.RegistrationService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    RegistrationRepository repository;

    public RegistrationServiceImpl(RegistrationRepository repository) {

        this.repository = repository;
    }

    @Override
    public Registration save(Registration registration) {
        if (repository.existsByEmailAndMeetup(registration.getEmail(), registration.getMeetup())) {
            throw new BusinessException("Registration already created");
        }

        return repository.save(registration);
    }

    @Override
    public Optional<Registration> getRegistrationById(Integer id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Registration registration) {
        if (registration == null || registration.getId() == null) {
            throw new IllegalArgumentException("Registration id cannot be null");
        }

        this.repository.delete(registration);
    }

    @Override
    public Registration update(Registration registration) {
        if (registration == null || registration.getId() == null) {
            throw new IllegalArgumentException("Registration id cannot be null");
        }

        return this.repository.save(registration);
    }

    @Override
    public Page<Registration> find(Registration filter, Pageable pageRequest) {
        Example<Registration>  example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example,pageRequest);
    }

    @Override
    public List<RegistrationDTO> getAll() {

        return repository.findAll().stream().map((registration) -> {
            RegistrationDTO dto = RegistrationDTO.builder()
                    .id(registration.getId())
                    .personName(registration.getPersonName())
                    .email(registration.getEmail())
                    .dateOfRegistration(registration.getDateOfRegistration())
                    .registered(registration.getRegistered())
                    .meetup(
                            MeetupDTO.builder()
                                    .id(registration.getMeetup().getId())
                                    .event(registration.getMeetup().getEvent())
                                    .description(registration.getMeetup().getDescription())
                                    .organizer(registration.getMeetup().getOrganizer())
                                    .meetupDate(registration.getMeetup().getMeetupDate())
                                    .address(registration.getMeetup().getAddress())
                                    .build()
                    )
                    .build();

            return dto;

        }).collect(Collectors.toList());

    }


    @Override
    public Page<Registration> getRegistrationsByMeetup(Meetup meetup, Pageable pageable) {
        return repository.findByMeetup(meetup, pageable);
    }
}
