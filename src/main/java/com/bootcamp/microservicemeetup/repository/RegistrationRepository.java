package com.bootcamp.microservicemeetup.repository;

import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Integer> {

    boolean existsById(Integer id);

    boolean existsByEmailAndMeetup(String email, Meetup meetup);

    Optional<Registration> findById(Integer integer);

//    Optional<Registration> findByRegistration(String registrationAttribute);

//    @Query( value = " select l from Registration as l " +
//            "where l.registered = :registered or l.person_name =:name ")
//    Page<Meetup> find(
//            @Param("registered") Boolean registered,
//            @Param("name") String name,
//            Pageable pageable
//    );

    Page<Registration> findByMeetup(Meetup meetup, Pageable pageable);
}
