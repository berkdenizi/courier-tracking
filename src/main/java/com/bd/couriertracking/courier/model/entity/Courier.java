package com.bd.couriertracking.courier.model.entity;

import com.bd.couriertracking.common.model.entity.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Entity
@Table(name = "courier",
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_courier_identity_number", columnNames = "identity_number"),
                @UniqueConstraint(name = "ux_courier_phone_number", columnNames = "phone_number")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Courier extends BaseAuditableEntity {

    @Column(name = "identity_number", nullable = false, length = 32)
    private String identityNumber;

    @Column(name = "phone_number", nullable = false, length = 32)
    private String phoneNumber;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "surname", nullable = false, length = 120)
    private String surname;
}
