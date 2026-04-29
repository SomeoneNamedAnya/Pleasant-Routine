package org.app.ref_info.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dormitory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dormitory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;
    private String region;
    private String city;

    private Long universityId;
}
