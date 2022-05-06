package com.bootcamp.microservicemeetup.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table(name = "meetup")
public class Meetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String event;

    @Column
    private String description;

    @Column
    private String organizer;

    @Column(name = "meetup_date")
    private String meetupDate;

    @Column
    private String address;

    @OneToMany(mappedBy = "meetup")
    private List<Registration> registrations;


}
