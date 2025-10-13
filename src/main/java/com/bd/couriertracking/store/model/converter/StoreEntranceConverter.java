package com.bd.couriertracking.store.model.converter;

import com.bd.couriertracking.store.model.dto.StoreEntranceDto;
import com.bd.couriertracking.store.model.entity.Store;
import com.bd.couriertracking.store.model.entity.StoreEntrance;
import com.bd.couriertracking.store.model.response.StoreEntranceResponse;
import org.springframework.stereotype.Component;

@Component
public class StoreEntranceConverter {

    public StoreEntranceDto entityToDto(StoreEntrance e) {
        if (e == null) return null;
        return StoreEntranceDto.builder()
                .id(e.getId())
                .storeId(e.getStore() != null ? e.getStore().getId() : null)
                .courierId(e.getCourierId())
                .entranceTime(e.getEntranceTime())
                .distanceMeters(e.getDistanceMeters())
                .createdAt(e.getCreatedAt())
                .createdBy(e.getCreatedBy())
                .modifiedAt(e.getModifiedAt())
                .modifiedBy(e.getModifiedBy())
                .build();
    }

    public StoreEntrance dtoToEntity(StoreEntranceDto d) {
        if (d == null) return null;

        Store storeRef = null;
        if (d.getStoreId() != null) {
            storeRef = new Store();
            storeRef.setId(d.getStoreId());
        }

        return StoreEntrance.builder()
                .id(d.getId())
                .store(storeRef)
                .courierId(d.getCourierId())
                .entranceTime(d.getEntranceTime())
                .distanceMeters(d.getDistanceMeters())
                .createdAt(d.getCreatedAt())
                .createdBy(d.getCreatedBy())
                .modifiedAt(d.getModifiedAt())
                .modifiedBy(d.getModifiedBy())
                .build();
    }

    public StoreEntranceResponse toResponse(StoreEntranceDto e) {
        if (e == null) return null;
        return StoreEntranceResponse.builder()
                .id(e.getId())
                .storeId(e.getId())
                .courierId(e.getCourierId())
                .entranceTime(e.getEntranceTime())
                .distanceMeters(e.getDistanceMeters())
                .createdAt(e.getCreatedAt())
                .createdBy(e.getCreatedBy())
                .modifiedAt(e.getModifiedAt())
                .modifiedBy(e.getModifiedBy())
                .build();
    }

}
