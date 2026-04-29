package org.app.ref_info.repository;

import org.app.ref_info.domain.Dormitory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DormitoryRepository extends JpaRepository<Dormitory, Long> {
}