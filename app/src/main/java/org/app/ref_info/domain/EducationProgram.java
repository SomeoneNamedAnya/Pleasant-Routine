package org.app.ref_info.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "education_program")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;
    private String qualification;
    private String department;
    private String level;
}
