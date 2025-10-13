package com.bd.couriertracking.courier.handler;

import com.bd.couriertracking.common.redis.repository.RedisRepository;
import com.bd.couriertracking.courier.event.CourierLocationEvent;
import com.bd.couriertracking.courier.model.entity.CourierLocation;
import com.bd.couriertracking.courier.repository.CourierLocationRepository;
import com.bd.couriertracking.store.service.StoreEntranceCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationEventHandlerTest {

    @Mock RedisRepository redis;
    @Mock
    StoreEntranceCommandService storeEntranceService;
    @Mock CourierLocationRepository locationRepo;

    @InjectMocks LocationEventHandler handler;

    @Test
    void onLocation_savesAndCallsEntrance() {
        long courierId = 7L;
        var e = new CourierLocationEvent(courierId, 41.0, 29.0,
                LocalDateTime.now(ZoneOffset.UTC));

        when(redis.withCourierLock(eq(courierId), anyLong(), any()))
                .thenAnswer(inv -> {
                    Supplier<?> s = inv.getArgument(2);
                    s.get();
                    return null;
                });

        when(redis.getLastLocation(courierId)).thenReturn(Optional.empty());
        when(locationRepo.existsByCourierIdAndEventTime(eq(courierId), any())).thenReturn(false);
        when(locationRepo.save(ArgumentMatchers.any(CourierLocation.class)))
                .thenAnswer(a -> a.getArgument(0));

        handler.onLocation(e);

        verify(locationRepo).save(any(CourierLocation.class));
        verify(storeEntranceService)
                .tryCreateEntrance(eq(courierId), eq(41.0), eq(29.0), any(LocalDateTime.class));
        verify(redis).saveLastLocation(eq(courierId), any());
    }
}
