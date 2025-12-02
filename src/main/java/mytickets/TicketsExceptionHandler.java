package mytickets;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class TicketsExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(TicketsExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleException(
            Exception exception
    ) {
        log.error("exception; message: {}", exception.getMessage());
        ErrorDTO error = new ErrorDTO(
                "Internal Server Error",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(500)
                .body(error);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorDTO> handleBadRequest(
            Exception exception
    ) {
        String message = exception.getMessage();
        if(exception instanceof MethodArgumentNotValidException){
             message = exception.getMessage(); //TODO: extract message from exception
        }
        log.error("request is wrong; message: {}", message);
        ErrorDTO error = new ErrorDTO(
                "Bad Request",
                message,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleNotFound(
            Exception exception
    ) {
        log.error("entity not found; message: {}", exception.getMessage());
        ErrorDTO error = new ErrorDTO(
                "Not Found",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
