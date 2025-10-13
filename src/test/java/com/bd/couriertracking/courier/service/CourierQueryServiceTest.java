package com.bd.couriertracking.courier.service;

import com.bd.couriertracking.common.api.PageResult;
import com.bd.couriertracking.common.exception.ResourceNotFoundException;
import com.bd.couriertracking.courier.model.converter.CourierMapper;
import com.bd.couriertracking.courier.model.dto.CourierDto;
import com.bd.couriertracking.courier.model.entity.Courier;
import com.bd.couriertracking.courier.model.response.CourierDistanceResponse;
import com.bd.couriertracking.courier.model.response.CourierResponse;
import com.bd.couriertracking.courier.repository.CourierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourierQueryServiceTest {

    @Mock CourierRepository courierRepository;
    @Mock CourierMapper mapper;
    @Mock CourierLocationService locationService;

    @InjectMocks CourierQueryService service;

    @Test
    void listPage_mapsToPageResult() {
        var e1 = new Courier(); e1.setId(1L);
        var e2 = new Courier(); e2.setId(2L);
        Page<Courier> pageEntities = new PageImpl<>(List.of(e1, e2), PageRequest.of(0, 2), 5);

        when(courierRepository.findAll(any(Pageable.class))).thenReturn(pageEntities);

        var d1 = new CourierDto(); d1.setId(1L);
        var d2 = new CourierDto(); d2.setId(2L);
        when(mapper.entityToDto(e1)).thenReturn(d1);
        when(mapper.entityToDto(e2)).thenReturn(d2);

        var r1 = CourierResponse.builder().id(1L).build();
        var r2 = CourierResponse.builder().id(2L).build();
        when(mapper.toResponse(d1)).thenReturn(r1);
        when(mapper.toResponse(d2)).thenReturn(r2);

        PageResult<CourierResponse> pr = service.listPage(PageRequest.of(0, 2));

        assertThat(pr.getItems()).hasSize(2);
        assertThat(pr.getTotalElements()).isEqualTo(5);
        assertThat(pr.getItems().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void get_returnsMappedCourier() {
        var entity = new Courier(); entity.setId(9L);
        when(courierRepository.findById(9L)).thenReturn(Optional.of(entity));

        var dto = new CourierDto(); dto.setId(9L);
        when(mapper.entityToDto(entity)).thenReturn(dto);

        var resp = CourierResponse.builder().id(9L).build();
        when(mapper.toResponse(dto)).thenReturn(resp);

        var out = service.get(9L);
        assertThat(out.getId()).isEqualTo(9L);
    }

    @Test
    void get_notFound_throws() {
        when(courierRepository.findById(404L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getTotalDistanceMeters_buildsResponseFromLocationService() {
        when(locationService.getTotalDistanceMeterByCourierId(5L)).thenReturn(777.7);
        CourierDistanceResponse r = service.getTotalDistanceMeters(5L);
        assertThat(r.getCourierId()).isEqualTo(5L);
        assertThat(r.getTotalMeters()).isEqualTo(777.7);
    }
}
