package mytickets.tickets.dbConnection;

import jakarta.transaction.Transactional;
import mytickets.tickets.models.Status;
import mytickets.tickets.models.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
            SELECT task.status
            FROM TaskEntity task
            WHERE task.id = :id
            """)
    Status getStatusById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("""
            UPDATE TaskEntity task
            SET task.status = :status
            WHERE task.id = :id
            """)
    void updateTaskStatusById(
            @Param("id") Long id,
            @Param("status") Status status
    );

    @Query("""
            SELECT task.assignedUserId
            FROM TaskEntity task
            WHERE task.id = :id
            """)
    Long getAssignedUserIdById(
            @Param("id") Long id
    );

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM TaskEntity t
        WHERE t.id = :id
        """)
    int deleteTaskById(
            @Param("id") Long id
    );
}
