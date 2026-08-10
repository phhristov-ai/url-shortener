package com.phh.url.url_shortener.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RateLimiter {

	private static final int MAX_REQUESTS = 3;
	private static final Duration WINDOW = Duration.ofMinutes(1);

	private final RedisTemplate<String, String> redisTemplate;

	public boolean isAllowed(String clientIp) {

		String key = "rate-limit:" + clientIp;

		Long requests = redisTemplate
				.opsForValue()
				.increment(key);

		if (requests != null && requests == 1) {
			redisTemplate.expire(key, WINDOW);
		}

		return requests != null && requests <= MAX_REQUESTS;
	}
}