package mytickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query("SELECT MAX(task.id) FROM TaskEntity task")
    Long getHighestId();
}
