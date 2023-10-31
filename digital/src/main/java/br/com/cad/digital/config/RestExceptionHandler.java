package br.com.cad.digital.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.cad.digital.models.RestValidationError;

@RestControllerAdvice
public class RestExceptionHandler {

    Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<RestValidationError>> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e){
        log.error("Erro de validação");
        List<RestValidationError> errors = new ArrayList<>();
        e.getFieldErrors()
        .forEach(v -> errors.add(new RestValidationError
        (v.getField(), v.getDefaultMessage())));
        return ResponseEntity.badRequest().body(errors);
    }
}