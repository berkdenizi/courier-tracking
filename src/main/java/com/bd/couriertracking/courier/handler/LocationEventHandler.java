package com.bd.couriertracking.courier.handler;

import com.bd.couriertracking.common.geo.GeoService;
import com.bd.couriertracking.common.redis.repository.RedisRepository;
import com.bd.couriertracking.courier.event.CourierLocationEvent;
import com.bd.couriertracking.courier.model.entity.CourierLocation;
import com.bd.couriertracking.courier.repository.CourierLocationRepository;
import com.bd.couriertracking.store.service.StoreEntranceCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationEventHandler {

    private final RedisRepository redis;
    private final StoreEntranceCommandService storeEntranceService;
    private final CourierLocationRepository locationRepository;
    private final GeoService geoService;

    @Value("${app.locks.per-courier-ttl-seconds:15}")
    private long lockLeaseSeconds;

    @Async
    @EventListener
    public void onLocation(CourierLocationEvent e) {
        redis.withCourierLock(e.courierId(), lockLeaseSeconds, () -> {

            var prevOpt = redis.getLastLocation(e.courierId());
            double segment = 0d;
            if (prevOpt.isPresent()) {
                var p = prevOpt.get();
                segment = geoService.distanceMeters(p.lat(), p.lng(), e.lat(), e.lng());
            }
            LocalDateTime time = e.time() != null ? e.time() : LocalDateTime.now(ZoneOffset.UTC);

            if (locationRepository.existsByCourierIdAndEventTime(e.courierId(), time)) {
                log.error("Courier location already exists!");
                return null;
            }

            var row = CourierLocation.builder()
                    .courierId(e.courierId())
                    .lat(e.lat())
                    .lng(e.lng())
                    .eventTime(time)
                    .segmentMeters(segment > 0 ? segment : null)
                    .build();
            row.setCreatedBy("SYSTEM");
            try {
                locationRepository.save(row);
            } catch (DataIntegrityViolationException dup) {
                return null;
            }

            log.info("Courier location created! CourierId={}", e.courierId());

            storeEntranceService.tryCreateEntrance(
                    e.courierId(), e.lat(), e.lng(), time
            );

            long epoch = time.toEpochSecond(ZoneOffset.UTC);
            redis.saveLastLocation(e.courierId(),
                    new RedisRepository.LastLoc(e.lat(), e.lng(), epoch));

            log.debug("location processed courier={} segment={}m ",
                    e.courierId(), Math.round(segment));

            return null;
        });
    }
}
