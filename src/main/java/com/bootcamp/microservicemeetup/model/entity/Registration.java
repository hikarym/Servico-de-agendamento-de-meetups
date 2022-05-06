package com.bootcamp.microservicemeetup.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "registration")
public class Registration {
    @Id
    @Column(name = "registration_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "person_name")
    private String personName;

    @Column(name = "email")
    private String email;

    @Column(name = "date_of_registration")
    private String dateOfRegistration;

    @Column
    private Boolean registered;

    @ManyToOne
    @JoinColumn(name = "id_meetup")
    // Muitos registrations para 1 meetup - registro só pode ir em um meetup
    private Meetup meetup;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Registration that = (Registration) o;
//        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(dateOfRegistration, that.dateOfRegistration) && Objects.equals(registration, that.registration);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, name, dateOfRegistration, registration);
//    }
}
