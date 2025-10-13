package com.bd.couriertracking.store.model.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreEntranceResponse {
    private Long id;
    private Long storeId;
    private String storeName;
    private Double storeLat;
    private Double storeLng;
    private Long courierId;
    private LocalDateTime entranceTime;
    private Double distanceMeters;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
}
