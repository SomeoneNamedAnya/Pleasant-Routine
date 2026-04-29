package org.app.task.repository;

import org.app.task.entity.TaskApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskApprovalRepository extends JpaRepository<TaskApprovalEntity, Long> {
    List<TaskApprovalEntity> findByTaskId(Long taskId);
    Optional<TaskApprovalEntity> findByTaskIdAndWatcherId(Long taskId, Long watcherId);
    long countByTaskIdAndApprovedTrue(Long taskId);
}
