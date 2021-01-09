package com.membership.hub.exception.advice;

import com.membership.hub.exception.MembershipExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class MembershipExceptionHandler {

    @ExceptionHandler({MembershipExistsException.class})
    public final ResponseEntity<String> handleMembershipExistsExceptions(Exception exception, WebRequest request) {
        return new ResponseEntity<>("Exception was: " + exception.getMessage(), HttpStatus.CONFLICT);
    }
}
