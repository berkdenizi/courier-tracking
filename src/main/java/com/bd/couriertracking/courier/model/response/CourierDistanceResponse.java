package com.bd.couriertracking.courier.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CourierDistanceResponse {
    private long courierId;
    private double totalMeters;
}
