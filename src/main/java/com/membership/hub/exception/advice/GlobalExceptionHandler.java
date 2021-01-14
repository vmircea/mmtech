package com.membership.hub.exception.advice;

import com.membership.hub.exception.BranchException;
import com.membership.hub.exception.MembershipException;
import com.membership.hub.exception.PaymentException;
import com.membership.hub.exception.ProjectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static String buildExceptionMessage;

    @ExceptionHandler({MembershipException.class})
    public final ResponseEntity<String> handleMembershipExceptions(MembershipException exception, WebRequest request) {
        return new ResponseEntity<>("Exception: " + exception.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({BranchException.class})
    public final ResponseEntity<String> handleBranchExceptions(BranchException exception, WebRequest request) {
        return new ResponseEntity<>("Exception: " + exception.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({PaymentException.class})
    public final ResponseEntity<String> handlePaymentExceptions(PaymentException exception, WebRequest request) {
        if (exception.getError() == PaymentException.PaymentErrors.BAD_CREDENTIALS) {
            return new ResponseEntity<>("Exception: " + exception.getError(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("Exception: " + exception.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ProjectException.class})
    public final ResponseEntity<String> handleProjectExceptions(ProjectException exception, WebRequest request) {
        return new ResponseEntity<>("Exception: " + exception.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public final ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException exception, HttpServletRequest httpServletRequest) {
        Map<String, String> errors = new HashMap<>();
        buildExceptionMessage = "";
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            buildExceptionMessage = String.format("%s%s", buildExceptionMessage, errors);
        });
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Violation of some constraint of input variables");
        apiError.setException(buildExceptionMessage);
        apiError.setPath(httpServletRequest.getRequestURI());
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
