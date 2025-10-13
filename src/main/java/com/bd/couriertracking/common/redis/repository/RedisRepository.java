package com.bd.couriertracking.common.redis.repository;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

public interface RedisRepository {

    record LastLoc(double lat, double lng, long tsEpochMillis) {
    }

    boolean tryCreateDedupKey(long courierId, long storeId, Duration ttl);

    Optional<LastLoc> getLastLocation(long courierId);

    void saveLastLocation(long courierId, LastLoc value);

    <T> T withCourierLock(long courierId, long leaseSeconds, Supplier<T> task);

    Optional<Long> getLastEntranceClientEpoch(long courierId, long storeId);
    void setLastEntranceClientEpoch(long courierId, long storeId, long epochMillis);

}
