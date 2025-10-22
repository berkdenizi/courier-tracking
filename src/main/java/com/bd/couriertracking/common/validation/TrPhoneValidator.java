package com.bd.couriertracking.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TrPhoneValidator implements ConstraintValidator<TrPhone, String> {
    private static final String PHONE_REGEX = "^\\+90\\d{10}$";
    private boolean required;

    @Override
    public void initialize(TrPhone ann) {
        this.required = ann.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null || value.trim().isEmpty()) {
            return !required;
        }
        return value.matches(PHONE_REGEX);
    }
}
