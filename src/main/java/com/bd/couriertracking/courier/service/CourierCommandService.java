package com.bd.couriertracking.courier.service;

import com.bd.couriertracking.common.exception.DuplicateResourceException;
import com.bd.couriertracking.courier.model.converter.CourierMapper;
import com.bd.couriertracking.courier.model.entity.Courier;
import com.bd.couriertracking.courier.model.request.CreateCourierRequest;
import com.bd.couriertracking.courier.model.response.CourierResponse;
import com.bd.couriertracking.courier.repository.CourierRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourierCommandService {
    private final CourierRepository courierRepository;
    private final CourierMapper mapper;

    @Transactional
    public CourierResponse create(CreateCourierRequest req) {
        var dto = mapper.toDto(req);

        if (courierRepository.existsByIdentityNumber(dto.getIdentityNumber())) {
            log.warn("Duplicate identityNumber attempt: {}", dto.getIdentityNumber());
            throw new DuplicateResourceException("Courier", "identityNumber", dto.getIdentityNumber());
        }
        if (courierRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            log.warn("Duplicate phoneNumber attempt: {}", dto.getPhoneNumber());
            throw new DuplicateResourceException("Courier", "phoneNumber", dto.getPhoneNumber());
        }

        Courier saved = courierRepository.save(mapper.dtoToEntity(dto));
        return mapper.toResponse(mapper.entityToDto(saved));
    }
}