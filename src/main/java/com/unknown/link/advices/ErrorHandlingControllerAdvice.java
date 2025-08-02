package com.unknown.link.advices;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@Slf4j
@Hidden
@ControllerAdvice
public class ErrorHandlingControllerAdvice {
    @ResponseBody
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e)
    {
        log.warn("handleNoSuchElementException:\n{}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleExistSuchElementException(DataIntegrityViolationException e)
    {
        String source = e.getStackTrace()[0].getMethodName();
        log.warn("handleExistSuchElementException:\n{}\nSource {}", e.getMessage(), source);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ArrayList<String>> handleValidationErrorException(MethodArgumentNotValidException e)
    {
        ArrayList<String> errors = new ArrayList<>();
        e.getBindingResult().getFieldErrors().forEach(er -> errors.add(er.getField()));
        log.warn("constraintViolationException in method {} with results:\n{}", e.getParameter(), errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}