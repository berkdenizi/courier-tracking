package com.bd.couriertracking.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxLengthValidator implements ConstraintValidator<MaxLength, String> {
    private int max;

    @Override
    public void initialize(MaxLength ann) {
        this.max = ann.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null) return true; // Required ayrı kontrol eder
        return value.length() <= max;
    }
}
