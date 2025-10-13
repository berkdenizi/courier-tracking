package com.bd.couriertracking.store.repository;

import com.bd.couriertracking.store.model.entity.StoreEntrance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StoreEntranceRepository extends JpaRepository<StoreEntrance, Long>, JpaSpecificationExecutor<StoreEntrance> {

}