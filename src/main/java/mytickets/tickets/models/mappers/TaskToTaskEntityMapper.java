package mytickets.tickets.models.mappers;

import mytickets.tickets.models.Task;
import mytickets.tickets.models.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskToTaskEntityMapper {

    public TaskEntity map(Task task){
        return new TaskEntity(
                task.id(),
                task.creatorId(),
                task.assignedUserId(),
                task.status(),
                task.creationDate(),
                task.deadlineDate(),
                task.priority()
        );
    }

}

