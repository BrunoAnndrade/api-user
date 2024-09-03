package com.brunoandrade.quarkussocial.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.core.Response;

import java.lang.constant.Constable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResponseError {
    public static final int UNPROCESSABLE_ENTITY_STATUS = 422;

    private String message;
    private Collection<FieldError> fieldErrors;

    public ResponseError(String message, Collection<FieldError> fieldErrors) {
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations) {
        List<FieldError> errors = violations
                .stream()
                .map(cv -> new FieldError(cv.getPropertyPath().toString(), cv.getMessage()))
                .collect(Collectors.toList());

        String message = "Validation Error";
        return new ResponseError(message, errors);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Collection<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Collection<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public Response withStatusCode(int statusCode) {
        return Response.status(statusCode).entity(this).build();
    }
}
