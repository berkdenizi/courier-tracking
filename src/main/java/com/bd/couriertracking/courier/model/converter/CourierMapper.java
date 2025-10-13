package com.bd.couriertracking.courier.model.converter;

import com.bd.couriertracking.courier.model.dto.CourierDto;
import com.bd.couriertracking.courier.model.entity.Courier;
import com.bd.couriertracking.courier.model.request.CreateCourierRequest;
import com.bd.couriertracking.courier.model.response.CourierResponse;
import org.springframework.stereotype.Component;

@Component
public class CourierMapper {

    public CourierDto toDto(CreateCourierRequest req) {
        if (req == null) return null;
        return CourierDto.builder()
                .identityNumber(req.getIdentityNumber())
                .phoneNumber(req.getPhoneNumber())
                .name(req.getName())
                .surname(req.getSurname())
                .build();
    }

    public CourierDto entityToDto(Courier c) {
        if (c == null) return null;
        return CourierDto.builder()
                .id(c.getId())
                .identityNumber(c.getIdentityNumber())
                .phoneNumber(c.getPhoneNumber())
                .name(c.getName())
                .surname(c.getSurname())
                .build();
    }

    public Courier dtoToEntity(CourierDto dto) {
        if (dto == null) return null;
        return Courier.builder()
                .identityNumber(dto.getIdentityNumber())
                .phoneNumber(dto.getPhoneNumber())
                .name(dto.getName())
                .surname(dto.getSurname())
                .build();
    }

    public CourierResponse toResponse(CourierDto dto) {
        if (dto == null) return null;
        return CourierResponse.builder()
                .id(dto.getId())
                .identityNumber(dto.getIdentityNumber())
                .phoneNumber(dto.getPhoneNumber())
                .name(dto.getName())
                .surname(dto.getSurname())
                .build();
    }
}
