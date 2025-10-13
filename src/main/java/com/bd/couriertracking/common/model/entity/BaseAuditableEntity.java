package com.bd.couriertracking.common.model.entity;

import com.bd.couriertracking.common.constants.ConstantUtil;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseAuditableEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    protected Long id;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    protected LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    protected String createdBy;

    @LastModifiedDate
    @Column(name = "modified_at")
    protected LocalDateTime modifiedAt;

    @LastModifiedBy
    @Column(name = "modified_by")
    protected String modifiedBy;

    @PrePersist
    protected void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (createdBy == null) createdBy = ConstantUtil.SYSTEM;
    }

    @PreUpdate
    protected void preUpdate() {
        modifiedAt = LocalDateTime.now();
    }
}