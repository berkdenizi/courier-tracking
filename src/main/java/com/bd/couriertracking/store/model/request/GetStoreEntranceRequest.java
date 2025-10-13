package com.bd.couriertracking.store.model.request;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetStoreEntranceRequest {

    private Long id;
    private Long courierId;
    private Long storeId;
    private LocalDateTime entranceTimeFrom;
    private LocalDateTime entranceTimeTo;
    private Double distanceMin;
    private Double distanceMax;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
    private LocalDateTime modifiedAtFrom;
    private LocalDateTime modifiedAtTo;

    @Builder.Default
    @Min(0)
    private Integer page = 0;

    @Builder.Default
    @Min(1)
    private Integer size = 20;

    @Builder.Default
    private String sortBy = "entranceTime";

    @Builder.Default
    private String sortDir = "DESC";
}
