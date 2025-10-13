package com.bd.couriertracking.store.service;

import com.bd.couriertracking.common.geo.GeoService;
import com.bd.couriertracking.common.redis.repository.RedisRepository;
import com.bd.couriertracking.store.model.entity.Store;
import com.bd.couriertracking.store.model.entity.StoreEntrance;
import com.bd.couriertracking.store.repository.StoreEntranceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreEntranceCommandServiceTest {

    @InjectMocks
    private StoreEntranceCommandService service;

    @Mock
    private StoreEntranceRepository storeEntranceRepository;

    @Mock
    private StoreService storeService;

    @Mock
    private RedisRepository redis;

    @Mock
    private GeoService geoService;

    @Captor
    private ArgumentCaptor<StoreEntrance> entranceCaptor;

    @BeforeEach
    void setUp() {
        // @Value alanlarını test için sabitleyelim
        ReflectionTestUtils.setField(service, "dedupTtlSeconds", 60L);
        ReflectionTestUtils.setField(service, "radiusMeters", 100.0);
        ReflectionTestUtils.setField(service, "clientWindowSeconds", 60L);
    }

    @Test
    void shouldDoNothing_whenNearestStoreIsNull() {
        // given
        when(storeService.findNearestWithinRadius(39.9, 29.8, 100.0)).thenReturn(null);

        // when
        service.tryCreateEntrance(123L, 39.9, 29.8, LocalDateTime.now());

        // then
        verify(storeEntranceRepository, never()).save(any());
        verify(redis, never()).tryCreateDedupKey(anyLong(), anyLong(), any());
        verify(redis, never()).setLastEntranceClientEpoch(anyLong(), anyLong(), anyLong());
    }

    @Test
    void shouldDoNothing_whenInsideClientWindow() {
        // given
        Store mockStore = mock(Store.class);
        when(mockStore.getId()).thenReturn(10L);
        when(storeService.findNearestWithinRadius(39.9, 29.8, 100.0)).thenReturn(mockStore);

        LocalDateTime clientTime = LocalDateTime.of(2025, 10, 13, 8, 0, 0);
        long clientEpoch = clientTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        // lastTs ile fark 10 sn (window 60 sn) → erken return
        when(redis.getLastEntranceClientEpoch(123L, 10L)).thenReturn(Optional.of(clientEpoch - 10_000));

        // when
        service.tryCreateEntrance(123L, 39.9, 29.8, clientTime);

        // then (hiçbir şey yapılmamalı)
        verify(redis, never()).tryCreateDedupKey(anyLong(), anyLong(), any());
        verify(storeEntranceRepository, never()).save(any());
        verify(redis, never()).setLastEntranceClientEpoch(anyLong(), anyLong(), anyLong());
    }

    @Test
    void shouldDoNothing_whenDedupKeyNotFirstInWindow() {
        // given
        Store mockStore = mock(Store.class);
        when(mockStore.getId()).thenReturn(10L);
        when(storeService.findNearestWithinRadius(39.9, 29.8, 100.0)).thenReturn(mockStore);

        LocalDateTime clientTime = LocalDateTime.of(2025, 10, 13, 8, 0, 0);
        when(redis.getLastEntranceClientEpoch(123L, 10L)).thenReturn(Optional.empty());
        when(redis.tryCreateDedupKey(eq(123L), eq(10L), any())).thenReturn(false);

        // when
        service.tryCreateEntrance(123L, 39.9, 29.8, clientTime);

        // then
        verify(storeEntranceRepository, never()).save(any());
        verify(redis, never()).setLastEntranceClientEpoch(anyLong(), anyLong(), anyLong());
    }

    @Test
    void shouldSaveEntranceAndSetRedis_whenHappyPath() {
        // given
        Store mockStore = mock(Store.class);
        when(mockStore.getId()).thenReturn(10L);
        when(mockStore.getLatitude()).thenReturn(40.0);
        when(mockStore.getLongitude()).thenReturn(30.0);
        when(storeService.findNearestWithinRadius(39.9, 29.8, 100.0)).thenReturn(mockStore);

        LocalDateTime clientTime = LocalDateTime.of(2025, 10, 13, 8, 0, 0);
        long expectedClientEpoch = clientTime.toInstant(ZoneOffset.UTC).toEpochMilli();

        when(redis.getLastEntranceClientEpoch(123L, 10L)).thenReturn(Optional.empty());
        when(redis.tryCreateDedupKey(eq(123L), eq(10L), any())).thenReturn(true);
        when(geoService.distanceMeters(39.9, 29.8, 40.0, 30.0)).thenReturn(12.34);

        // when
        service.tryCreateEntrance(123L, 39.9, 29.8, clientTime);

        // then
        verify(storeEntranceRepository).save(entranceCaptor.capture());
        StoreEntrance saved = entranceCaptor.getValue();

        assertThat(saved.getStore()).isEqualTo(mockStore);
        assertThat(saved.getCourierId()).isEqualTo(123L);
        assertThat(saved.getEntranceTime()).isEqualTo(clientTime);
        assertThat(saved.getDistanceMeters()).isNotNull();
        assertThat(saved.getDistanceMeters()).isCloseTo(12.34, offset(0.0001));

        verify(redis).setLastEntranceClientEpoch(123L, 10L, expectedClientEpoch);
    }

    @Test
    void shouldUseNowWhenTimeIsNull_andStillWork() {
        // Bu test sadece akışın bozulmadığını kontrol eder (epoch değişken olduğu için sadece çağrıları doğruluyoruz)
        Store mockStore = mock(Store.class);
        when(mockStore.getId()).thenReturn(10L);
        when(mockStore.getLatitude()).thenReturn(40.0);
        when(mockStore.getLongitude()).thenReturn(30.0);
        when(storeService.findNearestWithinRadius(39.9, 29.8, 100.0)).thenReturn(mockStore);

        when(redis.getLastEntranceClientEpoch(123L, 10L)).thenReturn(Optional.empty());
        when(redis.tryCreateDedupKey(eq(123L), eq(10L), any())).thenReturn(true);
        when(geoService.distanceMeters(39.9, 29.8, 40.0, 30.0)).thenReturn(5.0);

        service.tryCreateEntrance(123L, 39.9, 29.8, null);

        verify(storeEntranceRepository).save(any(StoreEntrance.class));
        // Epoch dinamik olduğu için sadece çağrıyı doğrulayalım
        verify(redis).setLastEntranceClientEpoch(eq(123L), eq(10L), anyLong());
    }
}
