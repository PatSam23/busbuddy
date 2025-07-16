package com.application.busbuddy.model;

import com.application.busbuddy.model.enums.BusType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Entity
@Table(name= "buses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bus {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String busNumber;

    @Enumerated(EnumType.STRING)
    private BusType busType;

    private int totalSeats;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @OneToMany(mappedBy =  "bus", cascade = CascadeType.ALL)
    private List<Schedule> schedules;
}
