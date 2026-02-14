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

    @Column(name = "photo_1_url", length = 500)
    private String photo1Url;

    @Column(name = "photo_2_url", length = 500)
    private String photo2Url;

    @Column(name = "photo_3_url", length = 500)
    private String photo3Url;

    // Convenience method to get all photos as list
    @Transient
    public List<String> getAllPhotos() {
        return List.of(
                photo1Url != null ? photo1Url : "",
                photo2Url != null ? photo2Url : "",
                photo3Url != null ? photo3Url : ""
        ).stream().filter(url -> !url.isEmpty()).toList();
    }

    // Convenience method to set photo by index
    @Transient
    public void setPhotoByIndex(int index, String photoUrl) {
        switch (index) {
            case 1 -> this.photo1Url = photoUrl;
            case 2 -> this.photo2Url = photoUrl;
            case 3 -> this.photo3Url = photoUrl;
            default -> throw new IllegalArgumentException("Bus photo index must be 1, 2, or 3");
        }
    }

    @Transient
    public String getPhotoByIndex(int index) {
        return switch (index) {
            case 1 -> photo1Url;
            case 2 -> photo2Url;
            case 3 -> photo3Url;
            default -> throw new IllegalArgumentException("Bus photo index must be 1, 2, or 3");
        };
    }
}
