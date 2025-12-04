package mytickets.tickets.dbConnection;

import jakarta.transaction.Transactional;
import mytickets.tickets.models.Priority;
import mytickets.tickets.models.Status;
import mytickets.tickets.models.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM TaskEntity task
        WHERE task.id = :id
        """)
    int deleteTaskById(
            @Param("id") Long id
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE TaskEntity task
            SET task.status = :status
            WHERE task.id = :id
            """)
    void updateStatusById(
            @Param("id") Long id,
            @Param("status") Status status
    );

    @Query("""
            SELECT task.status
            FROM TaskEntity task
            WHERE task.id = :id
            """)
    Status getStatusById(@Param("id") Long id);

    @Query("""
            SELECT task.assignedUserId
            FROM TaskEntity task
            WHERE task.id = :id
            """)
    Long getAssignedUserIdById(
            @Param("id") Long id
    );

    @Query("""
            SELECT COUNT(task) FROM TaskEntity task
            WHERE task.assignedUserId = :assignedUserId
            AND task.status = :status
            """)
    Long getAmountOfTasksByAssignedUserIdAndStatus(
            @Param("assignedUserId") Long assignedUserId,
            @Param("status") Status status
    );

    @Query ("""
        SELECT task FROM TaskEntity task
        WHERE (:assignedUserId IS NULL OR task.assignedUserId = :assignedUserId)
        AND (:creatorId IS NULL OR task.creatorId = :creatorId)
        AND (:status IS NULL OR task.status = :status)
        AND (:priority IS NULL OR task.priority = :priority)
    """)
    Page<TaskEntity> findAllTasksByFilters(
            @Param("assignedUserId") Long  assignedUserId,
            @Param("creatorId") Long creatorId,
            @Param("status") Status status,
            @Param("priority") Priority priority,
            Pageable pageable
    );
    /*
     * let's take a closer look on expression
     *   :param is NULL OR task.param = :param
     *
     * let's write it like this for clarity
     *   param == null || task.param = param
     *
     * if param == null is true, then the whole expression is true (because of the OR gate),
     * if the whole expression is true, then the exact line that we are comparing right now passes the whole expression
     *
     * if param == null is false, then it tries to compare task.param and param (because of the OR gate).
     * then a result of the comparison is a result of the whole expression
     * then the exact line that we are comparing right now line goes through filter
     */
}
