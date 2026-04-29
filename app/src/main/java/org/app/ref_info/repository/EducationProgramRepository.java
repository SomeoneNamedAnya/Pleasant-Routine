package org.app.ref_info.repository;

import org.app.ref_info.domain.EducationProgram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationProgramRepository extends JpaRepository<EducationProgram, Long> {
}