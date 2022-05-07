package pl.createcompetition.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import pl.createcompetition.payload.responses.ErrorResponse;

import java.util.Collections;


@ControllerAdvice
public class ErrorResponseHandler {

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity requestMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        String errorMessage = ex.getMethod() + " is not supported on this endpoint";
        ErrorResponse errorResponse = new ErrorResponse.Builder(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage())
                .errorsList(Collections.singletonList(errorMessage))
                .build();

        return new ResponseEntity<Object>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
