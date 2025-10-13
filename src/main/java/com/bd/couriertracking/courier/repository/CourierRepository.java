package com.bd.couriertracking.courier.repository;

import com.bd.couriertracking.courier.model.entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierRepository extends JpaRepository<Courier, Long> {
    boolean existsByIdentityNumber(String identityNumber);

    boolean existsByPhoneNumber(String phoneNumber);
}
