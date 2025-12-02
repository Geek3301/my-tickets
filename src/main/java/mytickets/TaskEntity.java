package mytickets;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;


import java.time.LocalDateTime;

@Table(name = "tasks")
@Entity
public class TaskEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "tasks_creator_id")
    private Long creatorId;

    @Column(name = "tasks_assigned_user_id")
    private Long assignedUserId;

    @Column(name = "tasks_status")
    private Status status;

    @Column(name = "tasks_creation_date")
    private LocalDateTime creationDate;

    @Column(name = "tasks_deadline_date")
    private LocalDateTime deadlineDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "tasks_priority")
    private Priority priority;

    public TaskEntity() {
    }

    public TaskEntity(
            Long creatorId,
            Long assignedUserId,
            Status status,
            LocalDateTime creationDate,
            LocalDateTime deadlineDate,
            Priority priority
    ) {
        this.priority = priority;
        this.deadlineDate = deadlineDate;
        this.creationDate = creationDate;
        this.status = status;
        this.assignedUserId = assignedUserId;
        this.creatorId = creatorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
