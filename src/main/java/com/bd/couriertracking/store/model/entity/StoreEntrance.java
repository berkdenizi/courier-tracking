package com.bd.couriertracking.store.model.entity;

import com.bd.couriertracking.common.model.entity.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "store_entrance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
public class StoreEntrance extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "courier_id", nullable = false)
    private Long courierId;

    @Column(name = "entrance_time", nullable = false)
    private LocalDateTime entranceTime;

    @Column(name = "distance_meters")
    private Double distanceMeters;
}

