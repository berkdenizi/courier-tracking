package com.bd.couriertracking.store.model.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class StoreEntranceDto {
    private Long id;
    private Long storeId;
    private Long courierId;
    private LocalDateTime entranceTime;
    private Double distanceMeters;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
}
