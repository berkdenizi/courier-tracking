package com.bd.couriertracking;

import com.bd.couriertracking.common.redis.repository.RedisRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class CourierTrackingApplicationTests {

	@MockitoBean
	RedisRepository redisRepository;

	@Test
	void contextLoads() {
	}

}
