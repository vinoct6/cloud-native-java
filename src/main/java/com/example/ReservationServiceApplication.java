package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.stream.Stream;

@EnableBinding(ReservationChannels.class)
@SpringBootApplication
@EnableDiscoveryClient
//@EnableEurekaClient //Technology specific
public class ReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
}


interface ReservationChannels {

    @Input
    SubscribableChannel input();
}

@MessageEndpoint
class ReservationProcessor {

    @Autowired
    private ReservationRepository repository;

    @ServiceActivator(inputChannel = "input")
    public void acceptNewReservation(Message<String> msg) {
        System.out.println(msg);
        this.repository.save(new Reservation(msg.getPayload()));
    }
}


@RestController
@RefreshScope
class MessageController {

    @Value("${message}")
    private String value;

    @RequestMapping(method = RequestMethod.GET, value = "/message")
    public String readMessage() {
        return value;
    }
}

/*
@RestController
class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @RequestMapping(method = RequestMethod.GET,value = "/reservations")
    public Collection<Reservation> getReservations(){
        return reservationRepository.findAll();
    }

}*/

@Component
class SampleCLR implements CommandLineRunner {

    @Autowired
    ReservationRepository reservationRepository;

    @Override
    public void run(String... strings) throws Exception {

        Stream.of("Vinoth kumar", "Saranya", "varshyth", "kutty", "Hooli").forEach(name -> reservationRepository.save(new Reservation(name)));
        reservationRepository.findAll().forEach(System.out::println);
    }
}

//Create a rep of type Reservation whose primary key is of type Long
@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation, Long> {
    /*
    This will automatically translate to
    select * from reservations where reservation_name= :name

    or

     I could create a custom query
     @Query("select r from Reservation r where r.reservationName=:name
     */
    @RestResource(path = "by-name")
    Collection<Reservation> findByReservationName(@Param("name") String name);
}

@Component
class MyIndicator implements HealthIndicator {

    private Health h = Health.status("this is up").build();

    // We can listen to events and change the health
   /* @Event(FooEvent.class)
    public void onEvent(FooEvent e){
            h = Health.down().build();
    }*/

    @Override
    public Health health() {
        return h;
    }
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
