package mytickets;

/*
 * DTO stands for Data Transfer Object (object that used only to transfer data)
 */

import java.time.LocalDateTime;

public record ErrorDTO(
        String error,
        String message,
        LocalDateTime time
) {}
