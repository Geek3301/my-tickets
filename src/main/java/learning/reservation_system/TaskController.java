package learning.reservation_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping("task/{id}")
    public Task getTaskById(@PathVariable Long id){
        log.info("Task with id " + id + " was found");
        return taskService.getTaskById(id);
    }

    @GetMapping("/tasks")
    public List<Task> getAllTasks(){
        log.info("Tasks were found");
        return taskService.getAllTasks();
    }
}
