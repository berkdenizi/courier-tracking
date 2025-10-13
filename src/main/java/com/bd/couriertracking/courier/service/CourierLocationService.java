package com.bd.couriertracking.courier.service;

import com.bd.couriertracking.courier.event.CourierLocationEvent;
import com.bd.couriertracking.courier.model.request.CourierLocationRequest;
import com.bd.couriertracking.courier.repository.CourierLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class CourierLocationService {
    private final ApplicationEventPublisher publisher;
    private final CourierLocationRepository courierLocationRepository;

    public void publishLocation(CourierLocationRequest req) {
        publisher.publishEvent(buildCourierLocationEvent(req));
    }

    public double getTotalDistanceMeterByCourierId(long courierId) {
        return courierLocationRepository.getTotalTravelDistance(courierId);
    }

    private CourierLocationEvent buildCourierLocationEvent(CourierLocationRequest req) {
        return new CourierLocationEvent(req.courierId(),
                req.lat(),
                req.lng(),
                req.time() != null ? req.time() :  LocalDateTime.now(ZoneOffset.UTC));
    }
}
