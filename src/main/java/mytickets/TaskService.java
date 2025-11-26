package mytickets;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service

public class TaskService {

    private final Map<Long, Task> tasks;
    private final AtomicLong taskIds;
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        tasks = new HashMap<>();
        taskIds = new AtomicLong(0L);
        this.taskRepository = taskRepository;
    }


    public ResponseEntity<Task> getTaskById(Long id)
    {
        Optional<TaskEntity> optionalTaskEntity = taskRepository.findById(id);
        if(optionalTaskEntity.isPresent()){
            TaskEntity taskEntity = optionalTaskEntity.get();
            Task task = new Task(
                    taskEntity.getId(),
                    taskEntity.getCreatorId(),
                    taskEntity.getAssignedUserId(),
                    taskEntity.getStatus(),
                    taskEntity.getCreateDateTime(),
                    taskEntity.getDeadlineDate(),
                    taskEntity.getPriority());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(task);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public List<Task> getAllTasks() {
        List<TaskEntity> allTasks = taskRepository.findAll();
        return allTasks.stream().map(it -> new Task(          // stream is kinda foreach; it means $this; map(it -> new Task()) creates Task for each TaskEntity
                it.getId(),                                             // allTasks.stream() - makes Stream<TaskEntity> from List<TaskEntity>
                it.getCreatorId(),                                      // Stream<TaskEntity>.map(it -> new Task()) - makes Stream<Task>
                it.getAssignedUserId(),                                 // Stream<Task>.toList() - makes List<Task>
                it.getStatus(),
                it.getCreateDateTime(),
                it.getDeadlineDate(),
                it.getPriority()
        )).toList();
/*      alternative way (does the same)
*       List<Task> tasks = new ArrayList<>();
*        for(TaskEntity taskEntity : allTasks){
*            Task newTask = new Task(
*                    taskEntity.getId(),
*                    taskEntity.getCreatorId(),
*                    taskEntity.getAssignedUserId(),
*                    taskEntity.getStatus(),
*                    taskEntity.getCreateDateTime(),
*                    taskEntity.getDeadlineDate(),
*                    taskEntity.getPriority()
*            );
*            tasks.add(newTask);
*        }
*        return tasks;
*/
    }


    public ResponseEntity<TaskEntity> createTask(Task newRequestBodyTask) {
        if(newRequestBodyTask.id() != null || newRequestBodyTask.status() != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        TaskEntity taskEntity = new TaskEntity(
                newRequestBodyTask.creatorId(),
                newRequestBodyTask.assignedUserId(),
                Status.CREATED,
                newRequestBodyTask.createDateTime(),
                newRequestBodyTask.deadlineDate(),
                newRequestBodyTask.priority());
        taskRepository.save(taskEntity);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskEntity);
    }

    public ResponseEntity<Status> approveUpdate(Task newRequestBodyTask, Long id){
        Optional<TaskEntity> optionalOldTaskEntity = taskRepository.findById(id);
        if(optionalOldTaskEntity.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        TaskEntity oldTaskEntity = optionalOldTaskEntity.get();
        if(newRequestBodyTask.id() != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(oldTaskEntity.getStatus() == Status.DONE){
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

    public ResponseEntity<TaskEntity> updateTask(Long id, Task newRequestBodyTask) {
        ResponseEntity<Status> answer = approveUpdate(newRequestBodyTask, id);
        if(answer.getStatusCode() != HttpStatus.OK){
            return ResponseEntity.status(answer.getStatusCode()).build();
        } else {
                TaskEntity newTaskEntity = new TaskEntity(
                        newRequestBodyTask.creatorId(),
                        newRequestBodyTask.assignedUserId(),
                        answer.getBody(),
                        newRequestBodyTask.createDateTime(),
                        newRequestBodyTask.deadlineDate(),
                        newRequestBodyTask.priority()
                );
                newTaskEntity.setId(id);
                taskRepository.save(newTaskEntity);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(newTaskEntity);
        }
    }

    public ResponseEntity<TaskEntity> deleteTask(Long id) {
        Optional<TaskEntity> optionalTaskEntity = taskRepository.findById(id);
        if(optionalTaskEntity.isPresent()){
            TaskEntity taskEntity = optionalTaskEntity.get();
            taskRepository.delete(taskEntity);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(taskEntity);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
