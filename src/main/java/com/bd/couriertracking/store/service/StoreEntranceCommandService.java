package com.bd.couriertracking.store.service;

import com.bd.couriertracking.common.geo.GeoService;
import com.bd.couriertracking.common.redis.repository.RedisRepository;
import com.bd.couriertracking.store.model.entity.Store;
import com.bd.couriertracking.store.model.entity.StoreEntrance;
import com.bd.couriertracking.store.repository.StoreEntranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class StoreEntranceCommandService {

    private final StoreEntranceRepository storeEntranceRepository;
    private final StoreService storeService;
    private final RedisRepository redis;
    private final GeoService geoService;

    @Value("${app.dedup.entrance-ttl-seconds:60}")
    private long dedupTtlSeconds;

    @Value("${app.geo.radius-meters:100}")
    private double radiusMeters;

    @Value("${app.dedup.client-window-seconds:60}")
    private long clientWindowSeconds;

    @Transactional
    public void tryCreateEntrance(long courierId, double lat, double lng, LocalDateTime time) {
        Store nearest = storeService.findNearestWithinRadius(lat, lng, radiusMeters);
        if (nearest == null) return;

        long clientEpoch = (time != null ? time.toInstant(ZoneOffset.UTC) : Instant.now())
                .toEpochMilli();

        var lastClientTsOpt = redis.getLastEntranceClientEpoch(courierId, nearest.getId());
        if (lastClientTsOpt.isPresent()) {
            long lastTs = lastClientTsOpt.get();
            if (Math.abs(clientEpoch - lastTs) < clientWindowSeconds * 1000L) {
                return;
            }
        }

        boolean firstInWindow = redis.tryCreateDedupKey(
                courierId, nearest.getId(), Duration.ofSeconds(dedupTtlSeconds));
        if (!firstInWindow) return;

        Double distToStore = geoService.distanceMeters(lat, lng, nearest.getLatitude(), nearest.getLongitude());

        var se = StoreEntrance.builder()
                .store(nearest)
                .courierId(courierId)
                .entranceTime(time)
                .distanceMeters(distToStore)
                .build();
        storeEntranceRepository.save(se);
        redis.setLastEntranceClientEpoch(courierId, nearest.getId(), clientEpoch);
    }

}