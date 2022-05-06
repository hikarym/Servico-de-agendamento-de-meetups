package com.bootcamp.microservicemeetup.controller.resource;

import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.dto.MeetupFilterDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.service.MeetupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meetup")
//@RequiredArgsConstructor
public class MeetupController {

    private MeetupService meetupService;

    private ModelMapper modelMapper;

    public MeetupController(MeetupService meetupService, ModelMapper modelMapper) {
        this.meetupService = meetupService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private Integer create(@RequestBody MeetupDTO meetupDTO) {
        Meetup entity = modelMapper.map(meetupDTO, Meetup.class);
        entity = meetupService.save(entity);

//        modelMapper.map(entity, MeetupDTO.class);
        return entity.getId();
    }

    @GetMapping("/find")
    public Page<MeetupDTO> find(MeetupFilterDTO dto, Pageable pageRequest) {

        Meetup filter = modelMapper.map(dto, Meetup.class);
        Page<Meetup> result = meetupService.find(filter, pageRequest);

        List<MeetupDTO> meetups = result
                .getContent()
                .stream()
                .map(entity -> {
                    MeetupDTO meetupDTO = modelMapper.map(entity, MeetupDTO.class);
                    return meetupDTO;
                }).collect(Collectors.toList());
        return new PageImpl<>(meetups, pageRequest, result.getTotalElements());

    }

    @GetMapping
    public List<MeetupDTO> getAll() {
        List<MeetupDTO> result = meetupService.getAll();

        return result;
    }

    @DeleteMapping("{meetupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByMeetupId(@PathVariable Integer meetupId) {

        Meetup meetup = meetupService.getMeetupById(meetupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "meetup id doesn't exist"));
        meetupService.delete(meetup);
    }

    @PutMapping("{meetupId}")
    @ResponseStatus(HttpStatus.OK)
    public MeetupDTO update(@PathVariable Integer meetupId, @RequestBody MeetupDTO meetupDTO) {

        return meetupService.getMeetupById(meetupId).map(meetup -> {
            meetup.setEvent(meetupDTO.getEvent());
            meetup.setDescription(meetupDTO.getDescription());
            meetup.setOrganizer(meetupDTO.getOrganizer());
            meetup.setMeetupDate(meetupDTO.getMeetupDate());
            meetup.setAddress(meetupDTO.getAddress());
            meetup =  meetupService.update(meetup);
            return modelMapper.map(meetup, MeetupDTO.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "meetup id doesn't exist"));

    }
}
