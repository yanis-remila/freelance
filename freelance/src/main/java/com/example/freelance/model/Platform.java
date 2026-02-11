package com.example.freelance.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "platform")
@Data
@NoArgsConstructor
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    @JoinColumn(name = "platform_id")
    private Set<Mission> missions = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "platform_id")
    private Set<Freelancer> freelancers = new HashSet<>();
}
