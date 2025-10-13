package com.bd.couriertracking.courier.repository;

import com.bd.couriertracking.courier.model.entity.CourierLocation;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface CourierLocationRepository extends JpaRepository<CourierLocation, Long> {
    @Query("select coalesce(sum(c.segmentMeters), 0) " +
            "from CourierLocation c " +
            "where c.courierId = :courierId")
    Double getTotalTravelDistance(@Param("courierId") long courierId);

    boolean existsByCourierIdAndEventTime(Long courierId, LocalDateTime eventTime);

}