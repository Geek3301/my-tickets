package mytickets.tickets.services;

import jakarta.persistence.EntityNotFoundException;
import mytickets.tickets.dbConnection.TaskRepository;
import mytickets.tickets.models.Status;
import mytickets.tickets.models.Task;
import mytickets.tickets.models.TaskEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public void validateAmountOfActiveTasks(Long assignedUserId){
        Long amountOfActiveTasks = taskRepository.getAmountOfTasksByAssignedUserIdAndStatus(assignedUserId, Status.IN_PROGRESS);
        if(amountOfActiveTasks > 4){
            throw new IllegalStateException("User has too many active tasks");
        }
    }

    public void validateDatesOrder(LocalDateTime creationTime, LocalDateTime deadlineTime){
        if(deadlineTime.isBefore(creationTime)){
            throw new IllegalArgumentException("Deadline date can't be before creation date");
        }
    }

    public void validateDeletion(int deletedRows) {
        if(deletedRows == 0){
            throw new EntityNotFoundException("Task not found");
        }
    }

    public void validateStatusNotSet(Status status){
        if(status != null){
            throw new IllegalArgumentException("Task status can't be set");
        }
    }

    public void validateStatusNotCancelled(Status status){
        if(status == Status.CANCELLED){
            throw new IllegalStateException("Task is already cancelled");
        }
    }

    public void validateStatusNotInProgress(Status status){
        if(status == Status.IN_PROGRESS){
            throw new IllegalStateException("Task is already in progress");
        }
    }

    public void validateStatusNotDone(Status status){
        if(status == Status.DONE){
            throw new IllegalStateException("Task is already done");
        }
    }

    public void validateStatusNotNull(Status status){
        if(status == null){
            throw new EntityNotFoundException("Task not found");
        }
    }

    public Status defineNewUpdateStatus(Status oldStatus, Status newStatus){
        if(oldStatus == Status.DONE){
            if(newStatus == Status.DONE){
                throw new IllegalStateException("Task with id " + oldStatus + " is already done");
            } else if (newStatus == null) {
                return Status.IN_PROGRESS;
            }
        }
        return newStatus;
    }

}
