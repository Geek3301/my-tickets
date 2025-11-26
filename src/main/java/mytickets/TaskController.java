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
    public ResponseEntity<Task> createTask(
            @RequestBody Task newRequestBodyTask
    ){
        return this.taskService.createTask(newRequestBodyTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @RequestBody Task newRequestBodyTask,
            @PathVariable Long id
    ){
        return this.taskService.updateTask(id, newRequestBodyTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(
            @PathVariable Long id
    ){
        return this.taskService.deleteTask(id);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Task> startTask (
            @PathVariable Long id
    ){
        return this.taskService.startTask(id);
    }
}
