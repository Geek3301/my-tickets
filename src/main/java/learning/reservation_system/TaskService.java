package learning.reservation_system;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service

public class TaskService {

    private final Map<Long, Task> tasks;
    private final AtomicLong taskIds;

    public TaskService(){
        tasks = new HashMap<>();
        taskIds = new AtomicLong(0L);
    }


    public Task getTaskById(Long id)
    {
        return tasks.get(id);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }


    public ResponseEntity<Task> createTask(Task newRequestBodyTask) {
    Task newTask = new Task(
            taskIds.incrementAndGet(),
            newRequestBodyTask.creatorId(),
            newRequestBodyTask.assignedUserId(),
            newRequestBodyTask.status(),
            newRequestBodyTask.createDateTime(),
            newRequestBodyTask.deadlineDate(),
            Priority.Low
    );
    tasks.put(newTask.id(), newTask);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(newTask);
    }

    public ResponseEntity<Task> updateTask(Long id, Task newRequestBodyTask) {
        Task newTask = new Task(
                id,
                newRequestBodyTask.creatorId(),
                newRequestBodyTask.assignedUserId(),
                newRequestBodyTask.status(),
                newRequestBodyTask.createDateTime(),
                newRequestBodyTask.deadlineDate(),
                newRequestBodyTask.priority()
        );
        tasks.put(id, newTask);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(newTask);
    }

    public ResponseEntity<Task> deleteTask(Long id) {
        ResponseEntity<Task> response = ResponseEntity
                .status(HttpStatus.OK)
                .body(tasks.get(id));
        tasks.remove(id);
        return response;
    }
}
