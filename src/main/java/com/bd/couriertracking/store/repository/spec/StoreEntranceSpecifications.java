package com.bd.couriertracking.store.repository.spec;

import com.bd.couriertracking.common.constants.ConstantUtil;
import com.bd.couriertracking.store.model.entity.StoreEntrance;
import com.bd.couriertracking.store.model.entity.StoreEntrance.Fields;
import com.bd.couriertracking.store.model.request.GetStoreEntranceRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class StoreEntranceSpecifications {
    private StoreEntranceSpecifications() {}

    public static Specification<StoreEntrance> buildSpec(GetStoreEntranceRequest req) {
        return Specification.allOf(
                idEq(req.getId()),
                courierIdEq(req.getCourierId()),
                storeIdEq(req.getStoreId()),
                entranceBetween(req.getEntranceTimeFrom() ,req.getEntranceTimeTo()),
                distanceBetween(req.getDistanceMin(), req.getDistanceMax()),
                createdBetween(req.getCreatedAtFrom(), req.getCreatedAtTo()),
                modifiedBetween(req.getModifiedAtFrom(), req.getModifiedAtTo())
        );
    }

    public static Specification<StoreEntrance> idEq(Long id) {
        return id == null ? null :
                (root, q, cb) -> cb.equal(root.<Long>get(ConstantUtil.FIELD_ID), id);
    }

    public static Specification<StoreEntrance> courierIdEq(Long courierId) {
        return courierId == null ? null :
                (root, q, cb) -> cb.equal(root.<Long>get(Fields.courierId), courierId);
    }

    public static Specification<StoreEntrance> storeIdEq(Long storeId) {
        return storeId == null ? null :
                (root, q, cb) ->cb.equal(root.get(ConstantUtil.FIELD_STORE).get(ConstantUtil.FIELD_ID), storeId);
    }

    public static Specification<StoreEntrance> entranceBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) return null;
        return (root, q, cb) -> {
            var p = root.<LocalDateTime>get(Fields.entranceTime);
            if (from != null && to != null) return cb.between(p, from, to);
            if (from != null) return cb.greaterThanOrEqualTo(p, from);
            return cb.lessThanOrEqualTo(p, to);
        };
    }

    public static Specification<StoreEntrance> distanceBetween(Double min, Double max) {
        if (min == null && max == null) return null;
        return (root, q, cb) -> {
            var p = root.<Double>get(Fields.distanceMeters);
            if (min != null && max != null) return cb.between(p, min, max);
            if (min != null) return cb.ge(p, min);
            return cb.le(p, max);
        };
    }

    public static Specification<StoreEntrance> createdBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) return null;
        return (root, q, cb) -> {
            var p = root.<LocalDateTime>get(ConstantUtil.FIELD_CREATED_AT);
            if (from != null && to != null) return cb.between(p, from, to);
            if (from != null) return cb.greaterThanOrEqualTo(p, from);
            return cb.lessThanOrEqualTo(p, to);
        };
    }

    public static Specification<StoreEntrance> modifiedBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) return null;
        return (root, q, cb) -> {
            var p = root.<LocalDateTime>get(ConstantUtil.FIELD_MODIFIED_AT);
            if (from != null && to != null) return cb.between(p, from, to);
            if (from != null) return cb.greaterThanOrEqualTo(p, from);
            return cb.lessThanOrEqualTo(p, to);
        };
    }
}
