package com.bd.couriertracking.common.api;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorInfo {
    private String code;
    private String message;
    private String field;
}