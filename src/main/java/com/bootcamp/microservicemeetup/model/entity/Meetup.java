package com.bootcamp.microservicemeetup.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Meetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String event;

    @JoinColumn(name = "id_registration")
    @ManyToOne
    // Muitos meetups para 1 registro - registro só pode ir em um meetup
    private Registration registration;

    @Column
    private String meetupDate;

    @Column
    private Boolean registered;
}
