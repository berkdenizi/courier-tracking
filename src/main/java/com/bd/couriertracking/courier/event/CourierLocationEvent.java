package com.bd.couriertracking.courier.event;

import java.time.LocalDateTime;

public record CourierLocationEvent(long courierId, double lat, double lng, LocalDateTime time) {
}
