package learning.reservation_system;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class TaskService {

    Map<Long, Task> tasksMap = Map.of
            (
            1L, new Task(
                    1L,
                    7L,
                    7L,
                    Status.DONE,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(5),
                    Priority.Medium
            ),
            2L, new Task(
                    2L,
                    7L,
                    7L,
                    Status.IN_PROGRESS,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(12),
                    Priority.High
            ),
            3L, new Task(
                    3L,
                    7L,
                    7L,
                    Status.CREATED,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(40),
                    Priority.Low
            )
    );

    HashMap<Long, Task> tasks = new HashMap<>(tasksMap);

    public Task getTaskById(Long id)
    {
        return tasks.get(id);
    }

    public List<Task> getAllTasks()
    {
        return List.of
                (
                tasks.get(1L),
                tasks.get(2L),
                tasks.get(3L)
                );
    }
}
