package com.bd.couriertracking.store.service;

import com.bd.couriertracking.common.geo.GeoService;
import com.bd.couriertracking.store.model.converter.StoreConverter;
import com.bd.couriertracking.store.model.entity.Store;
import com.bd.couriertracking.store.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    StoreRepository storeRepository;
    @Mock
    StoreConverter storeConverter;
    @Mock
    GeoService geoService;

    @InjectMocks
    StoreService storeService;

    @Test
    void findNearestWithinRadius_returnsNearest_whenInsideRadius() {
        double lat = 41.015137;
        double lng = 28.979530;
        double radius = 200;

        Store a = Store.builder().id(1L).name("A")
                .latitude(lat + 0.0005).longitude(lng).build();
        Store b = Store.builder().id(2L).name("B")
                .latitude(lat + 0.0018).longitude(lng).build();
        Store c = Store.builder().id(3L).name("C")
                .latitude(lat).longitude(lng + 0.0025).build();
        when(storeRepository.findByLatitudeBetweenAndLongitudeBetween(
                ArgumentMatchers.anyDouble(), ArgumentMatchers.anyDouble(),
                ArgumentMatchers.anyDouble(), ArgumentMatchers.anyDouble()))
                .thenReturn(List.of(a, b, c));

        Store nearest = storeService.findNearestWithinRadius(lat, lng, radius);

        assertThat(nearest).isNotNull();
        assertThat(nearest.getId()).isEqualTo(1L);
    }
}
