package com.bootcamp.microservicemeetup.controller.resource;

import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.dto.RegistrationDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.service.MeetupService;
import com.bootcamp.microservicemeetup.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/registration")
//@RequiredArgsConstructor
public class RegistrationController {

    private MeetupService meetupService;
    private RegistrationService registrationService;
    private ModelMapper modelMapper;

    public RegistrationController(MeetupService meetupService, RegistrationService registrationService, ModelMapper modelMapper) {
        this.meetupService = meetupService;
        this.registrationService= registrationService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationDTO create(@RequestBody @Valid RegistrationDTO registrationDTO) {

        Meetup meetup = meetupService.getMeetupById(registrationDTO.getMeetup().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "meetup id doesn't exist"));

        Registration entity = Registration.builder()
                .personName(registrationDTO.getPersonName())
                .email(registrationDTO.getEmail())
                .dateOfRegistration(registrationDTO.getDateOfRegistration())
                .registered(registrationDTO.getRegistered())
                .meetup(meetup)
                .build();

        entity = registrationService.save(entity);
        return modelMapper.map(entity, RegistrationDTO.class);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public RegistrationDTO getByRegistrationId(@PathVariable Integer id) {
        // pathVariable (na URL)
        return registrationService
                .getRegistrationById(id)
                .map(registration -> modelMapper.map(registration, RegistrationDTO.class))
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "registration id doesn't exist"));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public RegistrationDTO update(@PathVariable Integer id, @RequestBody RegistrationDTO registrationDTO) {

        Meetup meetup = meetupService.getMeetupById(registrationDTO.getMeetup().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        return registrationService.getRegistrationById(id)
                .map(registration -> {
                    registration.setPersonName(registrationDTO.getPersonName());
                    registration.setEmail(registrationDTO.getEmail());
                    registration.setDateOfRegistration(registrationDTO.getDateOfRegistration());
                    registration.setRegistered(registrationDTO.getRegistered());
                    registration.setMeetup(meetup);

                    registration = registrationService.update(registration);

                    return modelMapper.map(registration, RegistrationDTO.class);
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "registration id doesn't exist"));
    }

    // implementacao do metodo find
    @GetMapping("/find")
    public Page<RegistrationDTO> find(RegistrationDTO dto, Pageable pageRequest) {

        Registration filter = modelMapper.map(dto, Registration.class);
        Page<Registration> result = registrationService.find(filter, pageRequest);

        List<RegistrationDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, RegistrationDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<>(list, pageRequest, result.getTotalElements());
    }

    @GetMapping
    public List<RegistrationDTO> getAll() {

        List<RegistrationDTO> result = registrationService.getAll();

        return result;
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByRegistrationId(@PathVariable Integer id) {

        Registration registration = registrationService
                .getRegistrationById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "registration id doesn't exist"));
        registrationService.delete(registration);

    }

    @GetMapping("/meetup-registrations")
    public Page<RegistrationDTO> getRegistrationsByMeetup(@RequestBody MeetupDTO meetupDTO, Pageable pageRequest) {

        Meetup meetup = modelMapper.map(meetupDTO, Meetup.class);
        Page<Registration> result = registrationService.getRegistrationsByMeetup(meetup, pageRequest);

        List<RegistrationDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, RegistrationDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<>(list, pageRequest, result.getTotalElements());
    }
}
