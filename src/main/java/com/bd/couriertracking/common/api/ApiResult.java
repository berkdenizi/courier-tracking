package com.bd.couriertracking.common.api;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResult<T> {
    private boolean success;
    private T data;
    private ErrorInfo error;

    public static <T> ApiResult<T> ok(T data) {
        return ApiResult.<T>builder().success(true).data(data).build();
    }

    public static <T> ApiResult<T> error(ErrorInfo err) {
        return ApiResult.<T>builder().success(false).error(err).build();
    }
}