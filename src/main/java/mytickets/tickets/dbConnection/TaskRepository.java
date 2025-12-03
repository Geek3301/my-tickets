package mytickets.tickets.dbConnection;

import mytickets.tickets.models.Status;
import mytickets.tickets.models.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query("""
            SELECT COUNT(task) FROM TaskEntity task
            WHERE task.assignedUserId = :assignedUserId
            AND task.status = :status
            """)
    Long getAmountOfTasksByAssignedUserIdAndStatus(
            @Param("assignedUserId") Long assignedUserId,
            @Param("status") Status status
            );
}
