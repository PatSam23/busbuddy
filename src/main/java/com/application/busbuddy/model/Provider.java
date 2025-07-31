package com.application.busbuddy.model;

import com.application.busbuddy.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provider {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Bus> buses;

    @Enumerated(EnumType.STRING)
    private Role role; // ✅ Add this
}
