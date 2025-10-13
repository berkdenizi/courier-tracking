package com.bd.couriertracking.courier.model.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CourierLocationRequest(
        @NotNull long courierId,
        @NotNull Double lat,
        @NotNull Double lng,
        LocalDateTime time
) {
}
