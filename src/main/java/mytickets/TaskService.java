package mytickets;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service

public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        List<TaskEntity> allTasks = taskRepository.findAll();
        log.info("Tasks were found");
        return allTasks.stream().map(this::mapToTask).toList();

        // x -> { return x+2; } - lambda expression, where x is argument and { return x+2; } is body of the function so it's like function(x){ return x+2 }
        // x -> x+2 - also lambda expression, where x is argument and x+2 is body of the function
        // it -> mapToTask(it) - lambda expression, where it($this in java) is argument and mapToTask(it) is body of the function
        // this:mapToTask just calls mapToTask(it) for every it($this)
        // .map() - creates new list, where each element is transformed by lambda expression (it($this) was TaskEntity and became Task)
        // .stream converts List<TaskEntity> to Stream<TaskEntity>, and .toList() converts transformed Stream<Task> to List<Task>

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
                    taskEntity.getCreationDate(),
                    taskEntity.getDeadlineDate(),
                    taskEntity.getPriority());
            log.info("Task with id {} was found", id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(task);
        } else {
            throw new EntityNotFoundException("Task with id " + id + " not found");
        }
    }

    public ResponseEntity<Task> createTask(Task newRequestBodyTask) {
        if(newRequestBodyTask.status() != null){
            throw new IllegalArgumentException("Task status can't be set");
        }
        if(newRequestBodyTask.deadlineDate().isBefore(newRequestBodyTask.creationDate())){
            throw new IllegalArgumentException("Deadline date can't be before creation date");
        }
        TaskEntity taskEntity = new TaskEntity(
                newRequestBodyTask.creatorId(),
                newRequestBodyTask.assignedUserId(),
                Status.CREATED,
                newRequestBodyTask.creationDate(),
                newRequestBodyTask.deadlineDate(),
                newRequestBodyTask.priority());
        taskRepository.save(taskEntity);
        log.info("Task created");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapToTask(taskEntity));
    }

    public ResponseEntity<Status> approveUpdate(Task newRequestBodyTask, Long id){
        Optional<TaskEntity> optionalOldTaskEntity = taskRepository.findById(id);
        if(optionalOldTaskEntity.isEmpty()){
            throw new EntityNotFoundException("Task with id " + id + " not found");
        }
        TaskEntity oldTaskEntity = optionalOldTaskEntity.get();
        if(oldTaskEntity.getStatus() == Status.DONE){
            if(newRequestBodyTask.status() == Status.DONE){
                throw new IllegalStateException("Task is already done");
            } else if (newRequestBodyTask.status() == null) {
                return ResponseEntity.status(HttpStatus.OK).body(Status.IN_PROGRESS);
            }
        }
        if(newRequestBodyTask.deadlineDate().isBefore(newRequestBodyTask.creationDate())){
            throw new IllegalArgumentException("Deadline date can't be before creation date");
        }
        return ResponseEntity.status(HttpStatus.OK).body(newRequestBodyTask.status());
    }

    public ResponseEntity<Task> updateTask(Long id, Task newRequestBodyTask) {
        ResponseEntity<Status> answer = approveUpdate(newRequestBodyTask, id);
        if(answer.getStatusCode() != HttpStatus.OK){
            return ResponseEntity.status(answer.getStatusCode()).build();
        } else {
                TaskEntity newTaskEntity = new TaskEntity(
                        newRequestBodyTask.creatorId(),
                        newRequestBodyTask.assignedUserId(),
                        answer.getBody(),
                        newRequestBodyTask.creationDate(),
                        newRequestBodyTask.deadlineDate(),
                        newRequestBodyTask.priority()
                );
                newTaskEntity.setId(id);
                taskRepository.save(newTaskEntity);
                log.info("Task updated");
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(mapToTask(newTaskEntity));
        }
    }

    public ResponseEntity<Task> deleteTask(Long id) {
        Optional<TaskEntity> optionalTaskEntity = taskRepository.findById(id);
        if(optionalTaskEntity.isPresent()){
            TaskEntity taskEntity = optionalTaskEntity.get();
            taskRepository.delete(taskEntity);
            log.info("Task deleted");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(mapToTask(taskEntity));
        } else {
            throw new EntityNotFoundException("Task with id " + id + " not found");
        }
    }

    public ResponseEntity<Task> cancelTask(Long id) {
        Optional<TaskEntity> optionalTaskEntity = taskRepository.findById(id);
        if(optionalTaskEntity.isPresent()){
            TaskEntity taskEntity = optionalTaskEntity.get();
            if(taskEntity.getStatus() == Status.CANCELLED){
                throw new IllegalStateException("Task is already cancelled");
            }
            taskEntity.setStatus(Status.CANCELLED);
            taskRepository.save(taskEntity);
            log.info("Task with id {} cancelled", id);
            return ResponseEntity.status(HttpStatus.OK).body(mapToTask(taskEntity));
        } else {
            throw new EntityNotFoundException("Task with id " + id + " not found");
        }
    }

    public ResponseEntity<Task> startTask(Long id) {
        Optional<TaskEntity> optionalTaskEntity = taskRepository.findById(id);
        if(optionalTaskEntity.isPresent()){
            TaskEntity taskEntity = optionalTaskEntity.get();
                int amountOfActiveTasks = taskRepository.getTasksByAssignedUserId(taskEntity.getAssignedUserId(), Status.IN_PROGRESS).size();
                if(amountOfActiveTasks < 5){
                    if(taskEntity.getStatus() == Status.IN_PROGRESS){
                        throw new IllegalStateException("Task with id is already in progress");
                    }
                    if (taskEntity.getStatus() == Status.DONE){
                        throw new IllegalStateException("Can't cancel task that is already done");
                    }
                    taskEntity.setStatus(Status.IN_PROGRESS);
                    taskRepository.save(taskEntity);
                    log.info("Task with id {} started", id);
                    return ResponseEntity.status(HttpStatus.OK).body(mapToTask(taskEntity));
                } else {
                    throw new IllegalStateException("User has too many active tasks");
                }
        } else {
            throw new EntityNotFoundException("Task with id " + id + " not found");
        }
    }

    public Task mapToTask(TaskEntity taskEntity){
        return new Task(
                taskEntity.getId(),
                taskEntity.getCreatorId(),
                taskEntity.getAssignedUserId(),
                taskEntity.getStatus(),
                taskEntity.getCreationDate(),
                taskEntity.getDeadlineDate(),
                taskEntity.getPriority()
        );
    }
}
