package mytickets.tickets.services;

import jakarta.persistence.EntityNotFoundException;
import mytickets.tickets.dbConnection.TaskRepository;
import mytickets.tickets.models.Status;
import mytickets.tickets.models.Task;
import mytickets.tickets.models.TaskEntity;
import org.springframework.stereotype.Service;

@Service
public class TaskValidationService {

    private final TaskRepository taskRepository;

    public TaskValidationService(
            TaskRepository taskRepository
    ){
        this.taskRepository = taskRepository;
    }

    public TaskEntity getTaskEntityByIdIfExists(Long id){
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
    }

    public void validateAmountOfActiveTasks(TaskEntity taskEntity){
        Long amountOfActiveTasks = taskRepository.getAmountOfTasksByAssignedUserIdAndStatus(taskEntity.getAssignedUserId(), Status.IN_PROGRESS);
        if(amountOfActiveTasks > 4){
            throw new IllegalStateException("User has too many active tasks");
        }
    }

    public void validateDatesOrder(Task newRequestBodyTask){
        if(newRequestBodyTask.deadlineDate().isBefore(newRequestBodyTask.creationDate())){
            throw new IllegalArgumentException("Deadline date can't be before creation date");
        }
    }

    public void validateStatusNotSet(Task newRequestBodyTask){
        if(newRequestBodyTask.status() != null){
            throw new IllegalArgumentException("Task status can't be set");
        }
    }

    public void validateStatusNotCancelled(TaskEntity taskEntity){
        if(taskEntity.getStatus() == Status.CANCELLED){
            throw new IllegalStateException("Task with id " + taskEntity.getId() + " is already cancelled");
        }
    }

    public void validateStatusNotInProgress(TaskEntity taskEntity){
        if(taskEntity.getStatus() == Status.IN_PROGRESS){
            throw new IllegalStateException("Task with id " + taskEntity.getId() + " is already in progress");
        }
    }

    public void validateStatusNotDone(TaskEntity taskEntity){
        if(taskEntity.getStatus() == Status.DONE){
            throw new IllegalStateException("Task with id " + taskEntity.getId() + " is already done");
        }
    }

    public Status defineNewUpdateStatus(TaskEntity oldTaskEntity, Task newRequestBodyTask){
        if(oldTaskEntity.getStatus() == Status.DONE){
            if(newRequestBodyTask.status() == Status.DONE){
                throw new IllegalStateException("Task with id " + oldTaskEntity.getId() + " is already done");
            } else if (newRequestBodyTask.status() == null) {
                return Status.IN_PROGRESS;
            }
        }
        return newRequestBodyTask.status();
    }

}
