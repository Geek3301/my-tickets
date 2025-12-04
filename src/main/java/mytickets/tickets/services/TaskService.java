package mytickets.tickets.services;

import mytickets.tickets.dbConnection.TaskRepository;
import mytickets.tickets.models.Status;
import mytickets.tickets.models.Task;
import mytickets.tickets.models.TaskEntity;
import mytickets.tickets.models.mappers.TaskEntityToTaskMapper;
import mytickets.tickets.models.mappers.TaskToTaskEntityMapper;
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
    private final TaskEntityToTaskMapper taskEntityToTaskMapper;
    private final TaskToTaskEntityMapper taskToTaskEntityMapper;
    private final TaskValidationService taskValidationService;

    public TaskService(
            TaskRepository taskRepository,
            TaskEntityToTaskMapper taskEntityToTaskMapper,
            TaskToTaskEntityMapper taskToTaskEntityMapper,
            TaskValidationService taskValidationService
    ){
        this.taskRepository = taskRepository;
        this.taskEntityToTaskMapper = taskEntityToTaskMapper;
        this.taskToTaskEntityMapper = taskToTaskEntityMapper;
        this.taskValidationService = taskValidationService;
    }

    public ResponseEntity<List<Task>> getAllTasks() {
        List<TaskEntity> allTasks = taskRepository.findAll();
        log.info("Tasks were found");
        return ResponseEntity
                .ok(
                        allTasks
                        .stream()
                        .map(taskEntityToTaskMapper::map)
                        .toList()
                );

        // x -> { return x+2; } - lambda expression, where x is argument and { return x+2; } is body of the function so it's like function(x){ return x+2 }
        // x -> x+2 - also lambda expression, where x is argument and x+2 is body of the function
        // it -> mapToTask(it) - lambda expression, where it($this in java) is argument and mapToTask(it) is body of the function
        // this:mapToTask just calls mapToTask(it) for every it($this), same as it -> mapToTask(it)
        // taskEntityToTaskMapper::map just calls taskEntityToTaskMapper.map(it) for every it($this), same as it -> taskEntityToTaskMapper.map(it)
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
        TaskEntity taskEntity = taskValidationService.getTaskEntityByIdIfExists(id);
        Task task = taskEntityToTaskMapper.map(taskEntity);
        log.info("Task with id {} was found", id);
        return ResponseEntity.ok(task);
    }

    public ResponseEntity<Task> createTask(Task newRequestBodyTask) {
        taskValidationService.validateStatusNotSet(newRequestBodyTask.status());
        taskValidationService.validateDatesOrder(newRequestBodyTask.creationDate(), newRequestBodyTask.deadlineDate());
        TaskEntity taskEntity = taskToTaskEntityMapper.map(newRequestBodyTask);
        taskEntity.setStatus(Status.CREATED);
        taskRepository.save(taskEntity);
        log.info("Task created");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskEntityToTaskMapper.map(taskEntity));
    }

    public ResponseEntity<Task> updateTask(Long id, Task newRequestBodyTask) {
        taskValidationService.validateDatesOrder(newRequestBodyTask.creationDate(), newRequestBodyTask.deadlineDate());
        Status oldStatus = taskRepository.getStatusById(id);
        taskValidationService.validateStatusNotNull(oldStatus);
        Status status = taskValidationService.defineNewUpdateStatus(oldStatus, newRequestBodyTask.status());
        TaskEntity newTaskEntity = taskToTaskEntityMapper.map(newRequestBodyTask);
        newTaskEntity.setStatus(status);
        newTaskEntity.setId(id);
        taskRepository.save(newTaskEntity);
        log.info("Task updated");
        return ResponseEntity.ok(taskEntityToTaskMapper.map(newTaskEntity));
    }

    public ResponseEntity<Task> deleteTask(Long id) {
        int deletedRows = taskRepository.deleteTaskById(id);
        taskValidationService.validateDeletion(deletedRows);
        log.info("Task with id {} was deleted", id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Task> cancelTask(Long id, String responseType) {
        if("task".equals(responseType)) {
            TaskEntity taskEntity = taskValidationService.getTaskEntityByIdIfExists(id);
            taskValidationService.validateStatusNotCancelled(taskEntity.getStatus());
            taskEntity.setStatus(Status.CANCELLED);
            taskRepository.save(taskEntity);
            log.info("Task with id {} cancelled, task returned", id);
            return ResponseEntity.ok(taskEntityToTaskMapper.map(taskEntity));
        } else {
            Status status = taskRepository.getStatusById(id);
            taskValidationService.validateStatusNotNull(status);
            taskValidationService.validateStatusNotCancelled(status);
            taskRepository.updateTaskStatusById(id, Status.CANCELLED);
            log.info("Task with id {} cancelled", id);
            return ResponseEntity.ok().build();
        }
    }

    public ResponseEntity<Task> startTask(Long id, String responseType) {
        if("task".equals(responseType)) {
            TaskEntity taskEntity = taskValidationService.getTaskEntityByIdIfExists(id);
            taskValidationService.validateAmountOfActiveTasks(taskEntity.getAssignedUserId());
            taskValidationService.validateStatusNotInProgress(taskEntity.getStatus());
            taskEntity.setStatus(Status.IN_PROGRESS);
            taskRepository.updateTaskStatusById(id, Status.IN_PROGRESS);
            log.info("Task with id {} started, task returned", id);
            return ResponseEntity.ok(taskEntityToTaskMapper.map(taskEntity));
        } else {
            Long assignedUserId = taskRepository.getAssignedUserIdById(id);
            Status status = taskRepository.getStatusById(id);
            taskValidationService.validateStatusNotNull(status);
            taskValidationService.validateAmountOfActiveTasks(assignedUserId);
            taskValidationService.validateStatusNotInProgress(status);
            taskRepository.updateTaskStatusById(id, Status.IN_PROGRESS);
            log.info("Task with id {} started", id);
            return ResponseEntity.ok().build();
        }
    }

    public ResponseEntity<Task> completeTask(Long id, String responseType) {
        if("task".equals(responseType)) {
            TaskEntity taskEntity = taskValidationService.getTaskEntityByIdIfExists(id);
            taskValidationService.validateStatusNotDone(taskEntity.getStatus());
            taskEntity.setStatus(Status.DONE);
            taskRepository.updateTaskStatusById(id, Status.DONE);
            log.info("Task with id {} completed, task returned", id);
            return ResponseEntity.ok(taskEntityToTaskMapper.map(taskEntity));
        } else {
            Status status = taskRepository.getStatusById(id);
            taskValidationService.validateStatusNotNull(status);
            taskValidationService.validateStatusNotDone(status);
            taskRepository.updateTaskStatusById(id, Status.DONE);
            log.info("Task with id {} completed", id);
            return ResponseEntity.ok().build();
        }
    }
}
