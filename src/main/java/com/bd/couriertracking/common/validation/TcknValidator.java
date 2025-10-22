package com.bd.couriertracking.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TcknValidator implements ConstraintValidator<Tckn, String> {
    private boolean required;

    @Override
    public void initialize(Tckn ann) {
        this.required = ann.required();
    }

    @Override
    public boolean isValid(String t, ConstraintValidatorContext ctx) {
        if (t == null || t.trim().isEmpty()) {
            return !required;
        }
        t = t.trim();
        if (t.length() != 11) return false;
        for (int i = 0; i < 11; i++) if (!Character.isDigit(t.charAt(i))) return false;
        if (t.charAt(0) == '0') return false;

        int[] d = t.chars().map(c -> c - '0').toArray();
        int oddSum = d[0] + d[2] + d[4] + d[6] + d[8];
        int evenSum = d[1] + d[3] + d[5] + d[7];
        int tenth = ((oddSum * 7) - evenSum) % 10;
        if (tenth < 0) tenth += 10;
        if (d[9] != tenth) return false;

        int sum10 = 0;
        for (int i = 0; i < 10; i++) sum10 += d[i];
        return d[10] == (sum10 % 10);
    }
}
