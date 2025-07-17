package com.banking.banking.exceptions;

import com.banking.banking.enums.ErrorCode;
import com.banking.banking.exceptions.custom.AccountAlreadyExistException;
import com.banking.banking.exceptions.custom.UserAlreadyExistException;
import com.banking.banking.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse<Map<String, String>> responseError = new ErrorResponse<>(
                ErrorCode.VALIDATION_ERROR.getCode(),
                errors,
                "Invalid inputs",
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.badRequest().body(responseError);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse<Map<String, String>>> onConstraintValidationException(
            ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        ErrorResponse<Map<String, String>> responseError = new ErrorResponse<>(
                ErrorCode.VALIDATION_ERROR.getCode(),
                errors,
                "Invalid inputs",
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.badRequest().body(responseError);
    }

    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse<Void>> userNotFoundException(Exception userNotFoundException) {
        ErrorResponse<Void> response = new ErrorResponse<>(
                ErrorCode.WRONG_USER_NAME_OR_PASSWORD.getCode(),
                null,
                "Invalid username or password",
                HttpStatus.UNAUTHORIZED.value()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    public ResponseEntity<ErrorResponse<Void>> authenticationException(AuthenticationException userNotFoundException) {
        ErrorResponse<Void> response = new ErrorResponse<>(
                ErrorCode.AUTHENTICATION_FAILED.getCode(),
                null,
                "You are not login.",
                HttpStatus.UNAUTHORIZED.value()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(value = UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse<Void>> handleValidationServerExceptions(
            UserAlreadyExistException ex) {

        ErrorResponse<Void> response = new ErrorResponse<>(
                ErrorCode.USER_ALREADY_EXISTS.getCode(),
                null,
                "Username already exists. Please choose a different username.",
                HttpStatus.CONFLICT.value()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(value = AccountAlreadyExistException.class)
    public ResponseEntity<ErrorResponse<Void>> handleValidationServerExceptions(
            AccountAlreadyExistException ex) {

        ErrorResponse<Void> response = new ErrorResponse<>(
                ErrorCode.ACCOUNT_ALREADY_EXISTS.name(),
                null,
                ex.getMessage(),
                HttpStatus.CONFLICT.value()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse<Void>> handleValidationServerExceptions(
            Exception ex) {

        ErrorResponse<Void> response = new ErrorResponse<>(
                ErrorCode.INTERNAL_ERROR.getCode(),
                null,
                "The request cannot be handled!",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}