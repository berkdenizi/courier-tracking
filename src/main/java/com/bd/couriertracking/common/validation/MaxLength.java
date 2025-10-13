package com.bd.couriertracking.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MaxLengthValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxLength {
    int value();

    String message() default "{msg.maxlength}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
