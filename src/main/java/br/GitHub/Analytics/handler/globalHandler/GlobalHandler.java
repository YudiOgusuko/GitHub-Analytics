package br.GitHub.Analytics.handler.globalHandler;

import br.GitHub.Analytics.handler.errorResponse.ErrorResponse;
import br.GitHub.Analytics.handler.exception.InternalServerException;
import br.GitHub.Analytics.handler.exception.NoContentException;
import br.GitHub.Analytics.handler.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.databind.exc.MismatchedInputException;

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

    @ExceptionHandler(MismatchedInputException.class)
    public ResponseEntity<ErrorResponse> mismatchedInputException(MismatchedInputException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Erro ao tentar desserializar o JSON.")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> missingServletRequestParameterMethod(MissingServletRequestParameterException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(String.format("O parâmetro obrigatório '%s' não foi informado.", e.getParameterName()))
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> noResourceFoundMethod(NoResourceFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(String.format("A requisição '%s' foi realizada incorretamente.", e.getHttpMethod()))
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> badRequestHandlerMethod(HttpClientErrorException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("A URL usada na chamada é inválida.")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }
}
