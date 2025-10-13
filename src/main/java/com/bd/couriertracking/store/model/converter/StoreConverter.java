package com.bd.couriertracking.store.model.converter;

import com.bd.couriertracking.store.model.dto.StoreDto;
import com.bd.couriertracking.store.model.entity.Store;
import com.bd.couriertracking.store.model.response.StoreResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StoreConverter {

    public Store dtoToEntity(StoreDto dto) {
        if (dto == null) {
            return null;
        }

        return Store.builder()
                .id(dto.getId())
                .name(dto.getName().trim())
                .latitude(dto.getLat())
                .longitude(dto.getLng())
                .createdAt(dto.getCreatedAt())
                .createdBy(dto.getCreatedBy())
                .modifiedAt(dto.getModifiedAt())
                .modifiedBy(dto.getModifiedBy())
                .build();
    }

    public StoreDto entityToDto(Store entity) {
        if (entity == null) {
            return null;
        }
        return StoreDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .lat(entity.getLatitude())
                .lng(entity.getLongitude())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .modifiedAt(entity.getModifiedAt())
                .modifiedBy(entity.getModifiedBy())
                .build();
    }

    public List<Store> dtoListToEntityList(List<StoreDto> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream()
                .map(this::dtoToEntity)
                .toList();
    }

    public StoreResponse toResponse(StoreDto dto) {
        if (dto == null) return null;
        return StoreResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .lat(dto.getLat())
                .lng(dto.getLng())
                .createdAt(dto.getCreatedAt())
                .createdBy(dto.getCreatedBy())
                .modifiedAt(dto.getModifiedAt())
                .modifiedBy(dto.getModifiedBy())
                .build();
    }
}