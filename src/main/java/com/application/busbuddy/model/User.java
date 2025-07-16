package com.application.busbuddy.model;
import com.application.busbuddy.model.enums.Role;
import com.sun.jdi.PrimitiveValue;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedBy;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private  String email;
    
    private String password;

    @Enumerated(EnumType.STRING)
    private Role Role;
}
