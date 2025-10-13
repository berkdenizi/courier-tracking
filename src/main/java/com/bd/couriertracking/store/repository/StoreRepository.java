package com.bd.couriertracking.store.repository;

import com.bd.couriertracking.store.model.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByLatitudeBetweenAndLongitudeBetween(
            double minLat, double maxLat, double minLng, double maxLng);
}
