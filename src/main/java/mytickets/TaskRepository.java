package mytickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query("SELECT task FROM TaskEntity task WHERE task.assignedUserId = :assignedUserId AND task.status = :status")
    List<TaskEntity> getTasksByAssignedUserId(
            @Param("assignedUserId") Long assignedUserId,
            @Param("status") Status status
            );
}
