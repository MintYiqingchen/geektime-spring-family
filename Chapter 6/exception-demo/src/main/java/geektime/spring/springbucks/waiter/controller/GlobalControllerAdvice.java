package geektime.spring.springbucks.waiter.controller;

import geektime.spring.springbucks.waiter.controller.exception.FormValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationExceptionHandler(ValidationException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("message", "出错了啦"+exception.getMessage());
        return map;
    }

    @ExceptionHandler(FormValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> formValidationHandler(FormValidationException e){
        Map<String, String> map = new HashMap<>();
        map.put("出错码", "001");
        map.put("message", "这是我自己定制的handler");
        return map;
    }
}
