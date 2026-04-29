package org.app.dormitory.repository;

import org.app.dormitory.domain.StudentLiving;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentLivingRepository extends JpaRepository<StudentLiving, Long> {

    Optional<StudentLiving> findByUserIdAndEndedAtIsNull(Long userId);
}
