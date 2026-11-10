package br.GitHub.Analytics.handler.globalHandler;

import br.GitHub.Analytics.handler.exception.InternalServerException;
import br.GitHub.Analytics.handler.exception.NoContentException;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import br.GitHub.Analytics.handler.errorResponse.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundException(NotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<ErrorResponse> noContentException(NoContentException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> internalServerException(InternalServerException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
