package com.bootcamp.microservicemeetup.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetupDTO {

    private Integer id;

    @NotEmpty
    private String event;
    private String description;

    @NotEmpty
    private String organizer;

    @NotEmpty
    private String meetupDate;
    private String address;
}
