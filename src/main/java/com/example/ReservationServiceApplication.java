package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.stream.Stream;

@SpringBootApplication
public class ReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
}


@Component
class SampleCLR implements CommandLineRunner {

    @Autowired
    ReservationRepository reservationRepository;

    @Override
    public void run(String... strings) throws Exception {

        Stream.of("Vinoth","Saranya","varshyth","kutty","Hooli").forEach(name -> reservationRepository.save(new Reservation(name)));
        reservationRepository.findAll().forEach(System.out::println);
    }
}

//Create a rep of type Reservation whose primary key is of type Long
interface ReservationRepository extends JpaRepository<Reservation, Long> {
    /*
    This will automatically translate to
    select * from reservations where reservation_name= :name

    or

     I could create a custom query
     @Query("select r from Reservation r where r.reservationName=:name
     */
    Collection<Reservation> findByReservationName(String name);
}

@Entity
class Reservation {


    @GeneratedValue
    @Id
    private Long id;

    @Column
    private String reservationName;

    Reservation() {
    }

    public Reservation(String reservationName) {
        this.reservationName = reservationName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReservationName() {
        return reservationName;
    }

    public void setReservationName(String reservationName) {
        this.reservationName = reservationName;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", reservationName='" + reservationName + '\'' +
                '}';
    }
}
