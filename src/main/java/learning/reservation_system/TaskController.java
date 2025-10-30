package learning.reservation_system;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.EntityResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
    public Task getTaskById(@PathVariable Long id){
        log.info("Task with id " + id + " was found");
        return taskService.getTaskById(id);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestBody Task newRequestBodyTask
    ){
        log.info("Task created");
        return this.taskService.createTask(newRequestBodyTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @RequestBody Task newRequestBodyTask,
            @PathVariable Long id
    ){
        log.info("Task updated");
        return this.taskService.updateTask(id, newRequestBodyTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(
            @PathVariable Long id
    ){
        log.info("Task deleted");
        return this.taskService.deleteTask(id);
    }
}
