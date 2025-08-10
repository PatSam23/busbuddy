package com.application.busbuddy.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    private String destination;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private LocalDate travelDate;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}
