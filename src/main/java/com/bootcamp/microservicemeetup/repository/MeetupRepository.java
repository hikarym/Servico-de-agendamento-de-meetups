package com.bootcamp.microservicemeetup.repository;

import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetupRepository extends JpaRepository<Meetup, Integer> {

    boolean existsById(Integer id);

//    @Query( value = " select l from Meetup as l " +
//            "join registration as b on b.id_meetup = l.id " +
//            "where  b.person_name =:name or l.event =:event ")
//    Page<Meetup> findByRegistrationOnMeetup(
//            @Param("name") String name,
//            @Param("event") String event,
//            Pageable pageable
//    );

    @Query( value = " select l from Meetup as l " +
            "where l.organizer = :organizer or l.event =:event ")
    Page<Meetup> find(
            @Param("organizer") String organizer,
            @Param("event") String event,
            Pageable pageable
    );


//    Page<Meetup> findByMeetup(Meetup meetup, Pageable pageable );
}

