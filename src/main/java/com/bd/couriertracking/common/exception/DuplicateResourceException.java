package com.bd.couriertracking.common.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {
    private final String resource;
    private final String field;
    private final String value;

    public DuplicateResourceException(String resource, String field, String value) {
        super(resource + " already exists for " + field + ": " + value);
        this.resource = resource;
        this.field = field;
        this.value = value;
    }
}