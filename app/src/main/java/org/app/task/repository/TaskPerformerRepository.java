package org.app.task.repository;

import org.app.task.entity.TaskPerformerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskPerformerRepository extends JpaRepository<TaskPerformerEntity, Long> {
    List<TaskPerformerEntity> findByTaskId(Long taskId);
    List<TaskPerformerEntity> findByUserId(Long userId);
}