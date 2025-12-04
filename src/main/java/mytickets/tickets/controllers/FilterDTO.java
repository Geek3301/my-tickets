package mytickets.tickets.controllers;

/*
 * DTO stands for Data Transfer Object (object that used only to transfer data)
 */

import mytickets.tickets.models.Priority;
import mytickets.tickets.models.Status;

public record FilterDTO(
        Long assignedUserId,
        Long creatorId,
        Status status,
        Priority priority,
        Integer pageSize,
        Integer pageNumber
) {
    public boolean hasFilters() {
        return assignedUserId() != null
            || creatorId() != null
            || status() != null
            || priority() != null
            || pageSize() != null
            || pageNumber() != null;
    }
}
