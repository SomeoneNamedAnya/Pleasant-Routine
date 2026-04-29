package org.app.task.repository;

import org.app.task.entity.TaskWatcherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskWatcherRepository extends JpaRepository<TaskWatcherEntity, Long> {
    List<TaskWatcherEntity> findByTaskId(Long taskId);
    List<TaskWatcherEntity> findByUserId(Long userId);
}