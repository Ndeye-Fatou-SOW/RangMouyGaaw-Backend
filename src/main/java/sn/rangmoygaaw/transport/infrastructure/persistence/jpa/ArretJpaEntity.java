package sn.rangmoygaaw.transport.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "arrets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArretJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 100)
    private String commune;

    private Double latitude;
    private Double longitude;
    private Boolean accessible = true;
}