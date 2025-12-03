package mytickets.tickets.models;

import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDateTime;

public record Task(
        @Null
        Long id,
        @NotNull
        Long creatorId,
        @NotNull
        Long assignedUserId,
        Status status,
        @FutureOrPresent
        @NotNull
        LocalDateTime creationDate,
        @FutureOrPresent
        @NotNull
        LocalDateTime deadlineDate,
        @NotNull
        Priority priority
) {}
