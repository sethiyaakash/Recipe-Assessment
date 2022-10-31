package com.recipes.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RecipeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleRecipeNotFoundException(RecipeNotFoundException ex){
        return buildErrorResponse(ex, ex.getStatus());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex){
        return buildErrorResponse(ex, ex.getStatus());
    }

    private ResponseEntity<Object> buildErrorResponse(Exception ex, HttpStatus status){
        ErrorResponse errorResponse = new ErrorResponse(status.value(), ex.getMessage(), LocalDateTime.now());
        log.info("ErrorResponse built for error: " + errorResponse.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }



}
