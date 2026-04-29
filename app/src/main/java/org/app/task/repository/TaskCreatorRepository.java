package org.app.task.repository;

import org.app.task.entity.TaskCreatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskCreatorRepository extends JpaRepository<TaskCreatorEntity, Long> {
    Optional<TaskCreatorEntity> findByTaskId(Long taskId);
    List<TaskCreatorEntity> findByUserId(Long userId);
}