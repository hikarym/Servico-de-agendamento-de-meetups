package com.bootcamp.microservicemeetup.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationDTO {

    private Integer id;

    @NotEmpty
    private String personName;

    @NotEmpty
    private String email;

    @NotEmpty
    private String dateOfRegistration;

    private Boolean registered;

    private MeetupDTO meetup;
}
