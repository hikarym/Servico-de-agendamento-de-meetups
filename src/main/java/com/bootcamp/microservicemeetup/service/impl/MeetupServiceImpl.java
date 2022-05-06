package com.bootcamp.microservicemeetup.service.impl;

import com.bootcamp.microservicemeetup.exception.BusinessException;
import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.repository.MeetupRepository;
import com.bootcamp.microservicemeetup.service.MeetupService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeetupServiceImpl implements MeetupService {

    private MeetupRepository repository;

    public MeetupServiceImpl(MeetupRepository repository) {
        this.repository = repository;
    }

    @Override
    public Meetup save(Meetup meetup) {
//        if (repository.existsById(meetup.getId())) {
//            throw new BusinessException("Meetup already created!");
//        }
        return repository.save(meetup);
    }

    @Override
    public Optional<Meetup> getMeetupById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Meetup update(Meetup meetup) {
        return repository.save(meetup);
    }

    @Override
    public Page<Meetup> find(Meetup filter, Pageable pageable) {

//        return repository.find( filter.getRegistered(), filter.getEvent(), pageable );
        Example<Meetup>  example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example,pageable);
    }

    @Override
    public List<MeetupDTO> getAll() {

        return repository.findAll().stream().map((meetup) -> {
            MeetupDTO dto = MeetupDTO.builder()
                    .id(meetup.getId())
                    .event(meetup.getEvent())
                    .description(meetup.getDescription())
                    .organizer(meetup.getOrganizer())
                    .meetupDate(meetup.getMeetupDate())
                    .address(meetup.getAddress())
                    .build();

            return dto;

        }).collect(Collectors.toList());

    }

    @Override
    public void delete(Meetup meetup) {
        if (meetup == null || meetup.getId() == null) {
            throw new IllegalArgumentException("MeetupId cant not be null");
        }

        this.repository.delete(meetup);
    }


}
