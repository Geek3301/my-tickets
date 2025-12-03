package mytickets.errorHandling;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

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
        if(exception instanceof MethodArgumentNotValidException notValidException){
            message = notValidException          // taking our notValidException object
                    .getBindingResult()         // gives a BindingResult object that contains a lot of FieldError objects (1 for each wrong field)
                    .getFieldErrors()          // we are unpacking the BindingResult object and saving all the FieldError objects to the List (so we get List<FieldError>)
                    .stream()                 // we are converting the List<FieldError> to a Stream<FieldError> to make operations with it
                    .map(error ->   // for each element of Stream<FieldError> create "error" which will contain FieldError itself
                    {return error.getField() + ": " + error.getDefaultMessage();    //for each "error" return a String that looks like Field + ": " + DefaultMessage
                    })                     // save each returned String to the new Stream<String>
                    .collect(             // transforms Stream<String> to another data type (depends on Collector that given as an argument)
                            Collectors.joining("; ")    // says that we want to build a big String out of all Strings in the Stream (using "; " as a separator)
                    );                  // finally getting String with all FieldErrors
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
