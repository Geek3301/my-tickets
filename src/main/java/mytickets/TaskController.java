package mytickets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")

public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAllTasks(){
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @PostMapping
    public ResponseEntity<TaskEntity> createTask(
            @RequestBody Task newRequestBodyTask
    ){
        return this.taskService.createTask(newRequestBodyTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskEntity> updateTask(
            @RequestBody Task newRequestBodyTask,
            @PathVariable Long id
    ){
        return this.taskService.updateTask(id, newRequestBodyTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskEntity> deleteTask(
            @PathVariable Long id
    ){
        return this.taskService.deleteTask(id);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<TaskEntity> startTask (
            @PathVariable Long id
    ){
        return this.taskService.startTask(id);
    }
}
