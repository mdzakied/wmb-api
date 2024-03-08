package com.enigma.wmb_api.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ValidationUtil {
    private final Validator validator;

    public <T> void validate(T o){
        Set<ConstraintViolation<T>> validate = validator.validate(o);

        if (!validate.isEmpty()) {
            throw new ConstraintViolationException(validate);
        }
    }
}
