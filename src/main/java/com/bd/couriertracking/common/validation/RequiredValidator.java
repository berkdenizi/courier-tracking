package com.bd.couriertracking.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredValidator implements ConstraintValidator<Required, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext ctx) {
        if (value == null) return false;
        if (value instanceof String s) return !s.trim().isEmpty();
        return true; // string dışı tiplerde null değilse yeterli
    }
}
