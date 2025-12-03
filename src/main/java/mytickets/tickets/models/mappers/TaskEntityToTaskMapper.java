package mytickets.tickets.models.mappers;

import mytickets.tickets.models.Task;
import mytickets.tickets.models.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskEntityToTaskMapper {

    public Task map(TaskEntity taskEntity){
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
