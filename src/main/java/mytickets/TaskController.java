package mytickets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")

public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAllTasks(){
        log.info("Tasks were found");
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id){
        log.info("Task with id " + id + " was found");
        return taskService.getTaskById(id);
    }

    @PostMapping
    public ResponseEntity<TaskEntity> createTask(
            @RequestBody Task newRequestBodyTask
    ){
        log.info("Task created");
        return this.taskService.createTask(newRequestBodyTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskEntity> updateTask(
            @RequestBody Task newRequestBodyTask,
            @PathVariable Long id
    ){
        log.info("Task updated");
        return this.taskService.updateTask(id, newRequestBodyTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskEntity> deleteTask(
            @PathVariable Long id
    ){
        log.info("Task deleted");
        return this.taskService.deleteTask(id);
    }
}
