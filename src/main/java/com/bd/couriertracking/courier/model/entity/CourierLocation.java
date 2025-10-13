package com.bd.couriertracking.courier.model.entity;

import com.bd.couriertracking.common.model.entity.BaseAuditableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "courier_location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierLocation extends BaseAuditableEntity {
    private Long courierId;
    private Double lat;
    private Double lng;
    private LocalDateTime eventTime;
    private Double segmentMeters;
}
