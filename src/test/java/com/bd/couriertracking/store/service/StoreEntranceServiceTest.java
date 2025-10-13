package com.bd.couriertracking.store.service;

import com.bd.couriertracking.common.geo.GeoService;
import com.bd.couriertracking.common.redis.repository.RedisRepository;
import com.bd.couriertracking.store.model.entity.Store;
import com.bd.couriertracking.store.model.entity.StoreEntrance;
import com.bd.couriertracking.store.repository.StoreEntranceRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreEntranceServiceTest {

    @Mock StoreEntranceRepository repository;
    @Mock RedisRepository redis;
    @Mock StoreService storeService;
    @Mock GeoService geoService;

    @InjectMocks
    StoreEntranceCommandService service;

    @BeforeEach
    void cfg() throws Exception {

        var dedupField = StoreEntranceCommandService.class.getDeclaredField("dedupTtlSeconds");
        dedupField.setAccessible(true);
        dedupField.set(service, 60L);

        var winField = StoreEntranceCommandService.class.getDeclaredField("clientWindowSeconds");
        winField.setAccessible(true);
        winField.set(service, 60L);

        var radField = StoreEntranceCommandService.class.getDeclaredField("radiusMeters");
        radField.setAccessible(true);
        radField.set(service, 100d);
    }

    @Test
    void createsEntrance_whenFirstWithinWindow() {
        long courierId = 1L;
        double lat = 41.0, lng = 29.0;
        LocalDateTime t = LocalDateTime.ofInstant(Instant.parse("2025-10-13T06:05:00Z"), ZoneOffset.UTC);

        Store store = Store.builder().id(10L).name("S").latitude(lat).longitude(lng).build();
        when(storeService.findNearestWithinRadius(lat, lng, 100d)).thenReturn(store);
        when(redis.getLastEntranceClientEpoch(courierId, store.getId())).thenReturn(Optional.empty());
        when(redis.tryCreateDedupKey(courierId, store.getId(), Duration.ofSeconds(60))).thenReturn(true);

        service.tryCreateEntrance(courierId, lat, lng, t);

        verify(repository, times(1)).save(ArgumentMatchers.any(StoreEntrance.class));
        verify(redis).setLastEntranceClientEpoch(eq(courierId), eq(store.getId()), anyLong());
    }

    @Test
    void skips_whenClientTimeWithin1MinuteOfLast() {
        long courierId = 1L;
        double lat = 41.0, lng = 29.0;
        LocalDateTime t = LocalDateTime.ofInstant(Instant.parse("2025-10-13T06:05:30Z"), ZoneOffset.UTC);

        Store store = Store.builder().id(10L).name("S").latitude(lat).longitude(lng).createdBy("system").createdAt(LocalDateTime.now()).build();
        when(storeService.findNearestWithinRadius(lat, lng, 100d)).thenReturn(store);

        long lastEpoch = Instant.parse("2025-10-13T06:05:00Z").toEpochMilli();
        when(redis.getLastEntranceClientEpoch(courierId, store.getId())).thenReturn(Optional.of(lastEpoch));

        service.tryCreateEntrance(courierId, lat, lng, t);

        verify(repository, never()).save(any());
        verify(redis, never()).tryCreateDedupKey(anyLong(), anyLong(), any());
    }

    @Test
    void skips_whenTtlDedupBlocks() {
        long courierId = 1L;
        double lat = 41.0, lng = 29.0;
        LocalDateTime t = LocalDateTime.ofInstant(Instant.parse("2025-10-13T06:06:30Z"), ZoneOffset.UTC);

        Store store = Store.builder().id(10L).name("S").latitude(lat).longitude(lng).build();
        when(storeService.findNearestWithinRadius(lat, lng, 100d)).thenReturn(store);

        when(redis.getLastEntranceClientEpoch(courierId, store.getId())).thenReturn(Optional.empty());
        when(redis.tryCreateDedupKey(courierId, store.getId(), Duration.ofSeconds(60))).thenReturn(false);

        service.tryCreateEntrance(courierId, lat, lng, t);

        verify(repository, never()).save(any());
        verify(redis, never()).setLastEntranceClientEpoch(anyLong(), anyLong(), anyLong());
    }
}
