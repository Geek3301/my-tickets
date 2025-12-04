package mytickets.tickets.controllers;

import mytickets.tickets.models.Task;
import mytickets.tickets.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<Task>> getAllTasks(){
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestBody @Valid Task newRequestBodyTask
    ){
        return this.taskService.createTask(newRequestBodyTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @RequestBody @Valid Task newRequestBodyTask,
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

    @PostMapping({
            "/{id}/cancel",
            "/{id}/cancel/{responseType}",
    })
    public ResponseEntity<Task> cancelTask (
            @PathVariable Long id,
            @PathVariable(required = false) String responseType
    ){
        return this.taskService.cancelTask(id, responseType);
    }

    @PostMapping({
            "/{id}/start",
            "/{id}/start/{responseType}",
    })
    public ResponseEntity<Task> startTask (
            @PathVariable Long id,
            @PathVariable(required = false) String responseType
    ){
        return this.taskService.startTask(id, responseType);
    }

    @PostMapping({
            "/{id}/complete",
            "/{id}/complete/{responseType}"
    })
    public ResponseEntity<Task> completeTask(
            @PathVariable Long id,
            @PathVariable(required = false) String responseType
    ) {
        return this.taskService.completeTask(id, responseType);
    }

}
