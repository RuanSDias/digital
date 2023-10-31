package br.com.cad.digital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class RestNotFoundException extends RuntimeException {
    public RestNotFoundException(String e){
        super(e);
    }
}
