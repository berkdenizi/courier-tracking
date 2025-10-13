package com.bd.couriertracking.courier.service;

import com.bd.couriertracking.courier.event.CourierLocationEvent;
import com.bd.couriertracking.courier.model.request.CourierLocationRequest;
import com.bd.couriertracking.courier.repository.CourierLocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourierLocationServiceTest {

    @Mock ApplicationEventPublisher publisher;
    @Mock CourierLocationRepository locationRepository;

    @InjectMocks CourierLocationService service;

    @Test
    void publishLocation_emitsEvent_withGivenTime() {
        var t = LocalDateTime.of(2025, 10, 13, 9, 5);
        var req = new CourierLocationRequest(1L, 41.0, 29.0, t);

        ArgumentCaptor<CourierLocationEvent> cap = ArgumentCaptor.forClass(CourierLocationEvent.class);

        service.publishLocation(req);

        verify(publisher).publishEvent(cap.capture());
        var ev = cap.getValue();
        assertThat(ev.courierId()).isEqualTo(1L);
        assertThat(ev.lat()).isEqualTo(41.0);
        assertThat(ev.lng()).isEqualTo(29.0);
        assertThat(ev.time()).isEqualTo(t);
    }

    @Test
    void getTotalDistance_delegatesToRepo() {
        when(locationRepository.getTotalTravelDistance(7L)).thenReturn(1234.5);
        double d = service.getTotalDistanceMeterByCourierId(7L);
        assertThat(d).isEqualTo(1234.5);
        verify(locationRepository).getTotalTravelDistance(7L);
    }

    @Test
    void publishLocation_usesNowWhenNullTime() {
        var req = new CourierLocationRequest(2L, 40.0, 30.0, null);
        // Zamanı net assert etmiyoruz; sadece event yayıldı mı kontrolü
        service.publishLocation(req);
        verify(publisher).publishEvent(any(CourierLocationEvent.class));
    }
}
