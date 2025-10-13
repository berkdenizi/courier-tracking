package com.bd.couriertracking.courier.service;

import com.bd.couriertracking.common.api.PageResult;
import com.bd.couriertracking.common.exception.ResourceNotFoundException;
import com.bd.couriertracking.courier.model.converter.CourierMapper;
import com.bd.couriertracking.courier.model.response.CourierDistanceResponse;
import com.bd.couriertracking.courier.model.response.CourierResponse;
import com.bd.couriertracking.courier.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourierQueryService {
    private final CourierRepository courierRepository;
    private final CourierMapper mapper;
    private final CourierLocationService courierLocationService;

    public PageResult<CourierResponse> listPage(Pageable pageable) {
        var page = courierRepository.findAll(pageable)
                .map(mapper::entityToDto)
                .map(mapper::toResponse);
        return PageResult.from(page);
    }

    public CourierResponse get(Long id) {
        var c = courierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courier", id));
        return mapper.toResponse(mapper.entityToDto(c));
    }

    public CourierDistanceResponse getTotalDistanceMeters(long courierId) {
        Double totalMeter = courierLocationService.getTotalDistanceMeterByCourierId(courierId);
        return buildCourierDistanceResponse(courierId, totalMeter);
    }

    private CourierDistanceResponse buildCourierDistanceResponse(long courierId, Double meters) {
        return CourierDistanceResponse.builder()
                .courierId(courierId)
                .totalMeters(meters)
                .build();
    }
}

