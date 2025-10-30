package learning.reservation_system;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service

public class TaskService {

    private final Map<Long, Task> tasks;
    private final AtomicLong taskIds;

    public TaskService(){
        tasks = new HashMap<>();
        taskIds = new AtomicLong(0L);
    }


    public ResponseEntity<Task> getTaskById(Long id)
    {
        if(!tasks.containsKey(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tasks.get(id));
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }


    public ResponseEntity<Task> createTask(Task newRequestBodyTask) {
        if(newRequestBodyTask.id() != null || newRequestBodyTask.status() != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Task newTask = new Task(
            taskIds.incrementAndGet(),
            newRequestBodyTask.creatorId(),
            newRequestBodyTask.assignedUserId(),
            Status.CREATED,
            newRequestBodyTask.createDateTime(),
            newRequestBodyTask.deadlineDate(),
            newRequestBodyTask.priority()
    );
    tasks.put(newTask.id(), newTask);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(newTask);
    }

    public ResponseEntity<Status> approveUpdate(Task newRequestBodyTask, Long id){
        if(!tasks.containsKey(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if(tasks.get(id).status() == Status.DONE){
            if(newRequestBodyTask.status() == Status.DONE){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } else if (newRequestBodyTask.status() == null) {
                Status status = Status.IN_PROGRESS;
                return ResponseEntity.status(HttpStatus.OK).body(status);
            }
        }
        Status status = newRequestBodyTask.status();
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }

    public ResponseEntity<Task> updateTask(Long id, Task newRequestBodyTask) {
        ResponseEntity<Status> answer = approveUpdate(newRequestBodyTask, id);
        if(answer.getStatusCode() != HttpStatus.OK){
            return ResponseEntity.status(answer.getStatusCode()).build();
        }
        Task newTask = new Task(
                id,
                newRequestBodyTask.creatorId(),
                newRequestBodyTask.assignedUserId(),
                answer.getBody(),
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
        if(!tasks.containsKey(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ResponseEntity<Task> response = ResponseEntity
                .status(HttpStatus.OK)
                .body(tasks.get(id));
        tasks.remove(id);
        return response;
    }
}
