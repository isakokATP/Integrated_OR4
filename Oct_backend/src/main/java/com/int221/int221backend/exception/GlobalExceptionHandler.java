package com.int221.int221backend.exception;

import com.int221.int221backend.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "not_found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<Object> handleInvalidValueException(InvalidValueException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "validation_failed");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getFieldErrors().forEach(error ->
//                errors.put(error.getField(), error.getDefaultMessage())
//        );
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", "validation_failed");
//        response.put("errors", errors);
//
//        return ResponseEntity.badRequest().body(response);
//    }

    @ExceptionHandler(DuplicateBrandNameException.class)
    public ResponseEntity<String> handleDuplicateBrandName(DuplicateBrandNameException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", ex.getStatusCode().value());
        errorBody.put("message", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(errorBody);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Missing required query parameter: " + ex.getParameterName());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> handleDuplicateResourceException(DuplicateResourceException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new java.util.Date());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String fieldName = error.getField();
            Object rejectedValue = error.getRejectedValue();

            // field 'email'
            if ("email".equals(fieldName)) {
                //white space check
                if (rejectedValue instanceof String rejectedEmail && !rejectedEmail.equals(rejectedEmail.trim())) {
                    return buildUnauthorizedResponse(
                            "Email format is invalid (contains leading/trailing whitespace)", request
                    );
                }
            }

            if ("password".equals(fieldName)) {
                if (" ".equals(rejectedValue)) {
                    return buildUnauthorizedResponse(
                            "Password format is invalid", request
                    );
                }
                //password exceed 14 cha
                if ("Size".equals(error.getCode())) {
                    return buildUnauthorizedResponse(
                            // คุณสามารถใช้ default message จาก DTO ได้ด้วย
                            error.getDefaultMessage(), request
                    );
                }
            }
        }
        //400 Bad Request
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "validation_failed");
        responseBody.put("errors", errors);

        return ResponseEntity.badRequest().body(responseBody);
    }
    private ResponseEntity<Object> buildUnauthorizedResponse(String message, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
}
