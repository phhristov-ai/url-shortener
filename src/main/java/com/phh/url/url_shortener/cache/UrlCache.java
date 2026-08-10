package com.phh.url.url_shortener.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class UrlCache {

	private static final String KEY_PREFIX = "url:";

	private final RedisTemplate<String, String> redisTemplate;

	public String get(String shortCode) {
		return redisTemplate.opsForValue()
				.get(KEY_PREFIX + shortCode);
	}

	public void put(
			String shortCode,
			String originalUrl,
			Duration ttl
	) {
		redisTemplate.opsForValue()
				.set(
						KEY_PREFIX + shortCode,
						originalUrl,
						ttl
				);
	}

	public void evict(String shortCode) {
		redisTemplate.delete(KEY_PREFIX + shortCode);
	}
}