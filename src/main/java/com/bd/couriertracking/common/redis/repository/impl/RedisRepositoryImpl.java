package com.bd.couriertracking.common.redis.repository.impl;

import com.bd.couriertracking.common.redis.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {

    private final StringRedisTemplate stringRedisTemplate;

    private String lastClientKey(long cId, long sId) {
        return "entrance:lastClientTs:%d:%d".formatted(cId, sId);
    }

    @Qualifier("lastLocRedisTemplate")
    private final RedisTemplate<String, LastLoc> lastLocRedisTemplate;

    private final RedissonClient redissonClient;

    @Value("${app.last-location.ttl-seconds:86400}")
    private long lastLocTtlSeconds;

    private String dedupKey(long courierId, long storeId) {
        return "dedup:entrance:%d:%d".formatted(courierId, storeId);
    }

    private String lastLocKey(long courierId) {
        return "courier:lastloc:" + courierId;
    }

    private String lockKey(long courierId) {
        return "lock:courier:" + courierId;
    }

    @Override
    public boolean tryCreateDedupKey(long courierId, long storeId, Duration ttl) {
        Boolean ok = stringRedisTemplate.opsForValue().setIfAbsent(dedupKey(courierId, storeId), "1", ttl);
        return Boolean.TRUE.equals(ok);
    }

    @Override
    public Optional<LastLoc> getLastLocation(long courierId) {
        return Optional.ofNullable(lastLocRedisTemplate.opsForValue().get(lastLocKey(courierId)));
    }

    @Override
    public void saveLastLocation(long courierId, LastLoc value) {
        if (lastLocTtlSeconds > 0) {
            lastLocRedisTemplate.opsForValue().set(lastLocKey(courierId), value, Duration.ofSeconds(lastLocTtlSeconds));
        } else {
            lastLocRedisTemplate.opsForValue().set(lastLocKey(courierId), value);
        }
    }

    @Override
    public <T> T withCourierLock(long courierId, long leaseSeconds, Supplier<T> task) {
        RLock lock = redissonClient.getLock(lockKey(courierId));
        boolean acquired = false;
        try {
            acquired = lock.tryLock(0, leaseSeconds, TimeUnit.SECONDS);
            if (!acquired) throw new IllegalStateException("Concurrent courier processing");
            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    @Override
    public Optional<Long> getLastEntranceClientEpoch(long courierId, long storeId) {
        String v = stringRedisTemplate.opsForValue().get(lastClientKey(courierId, storeId));
        return v == null ? Optional.empty() : Optional.of(Long.parseLong(v));
    }

    @Override
    public void setLastEntranceClientEpoch(long courierId, long storeId, long epochMillis) {
        stringRedisTemplate.opsForValue().set(lastClientKey(courierId, storeId), Long.toString(epochMillis));
    }

}