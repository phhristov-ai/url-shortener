package com.phh.url.url_shortener.service;

import com.phh.url.url_shortener.cache.UrlCache;
import com.phh.url.url_shortener.dto.CreateUrlRequest;
import com.phh.url.url_shortener.dto.CreateUrlResponse;
import com.phh.url.url_shortener.entity.ShortUrl;
import com.phh.url.url_shortener.kafka.ClickEventProducer;
import com.phh.url.url_shortener.kafka.UrlClickedEvent;
import com.phh.url.url_shortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UrlService {

	private final UrlRepository urlRepository;
	private final Base62Encoder base62Encoder;
	private final SnowflakeIdGenerator snowflakeIdGenerator;
	private final UrlCache urlCache;
	private final ClickEventProducer clickEventProducer;

	@Transactional
	public CreateUrlResponse shortenUrl(CreateUrlRequest request) {

		validateUrl(request.url());

		validateExpiration(request.expiresAt());

		long id = snowflakeIdGenerator.nextId();

		String shortCode = base62Encoder.encode(id);

		ShortUrl shortUrl = new ShortUrl(
				id,
				shortCode,
				request.url(),
				request.expiresAt()
		);

		urlRepository.save(shortUrl);

		return new CreateUrlResponse(
				"http://localhost:8080/" + shortCode
		);
	}

	@Transactional(readOnly = true)
	public String getOriginalUrl(String shortCode) {

		String cachedUrl = urlCache.get(shortCode);

		if (cachedUrl != null) {
			publishClickEvent(shortCode);
			return cachedUrl;
		}

		ShortUrl shortUrl = urlRepository.findByShortCode(shortCode)
				.orElseThrow(() ->
						new IllegalArgumentException("Short URL not found")
				);

		if (shortUrl.getExpiresAt() != null
				&& !shortUrl.getExpiresAt().isAfter(Instant.now())) {

			urlCache.evict(shortCode);

			throw new IllegalArgumentException(
					"Short URL has expired"
			);
		}

		String originalUrl = shortUrl.getOriginalUrl();

		Duration ttl = calculateCacheTtl(
				shortUrl.getExpiresAt()
		);

		urlCache.put(
				shortCode,
				originalUrl,
				ttl
		);

		return originalUrl;
	}

	private void publishClickEvent(String shortCode) {

		clickEventProducer.send(
				new UrlClickedEvent(
						shortCode,
						Instant.now()
				)
		);
	}

	private void validateUrl(String url) {

		try {
			URI uri = URI.create(url);

			if (!"http".equalsIgnoreCase(uri.getScheme())
					&& !"https".equalsIgnoreCase(uri.getScheme())) {
				throw new IllegalArgumentException("URL must use HTTP or HTTPS");
			}

			if (uri.getHost() == null) {
				throw new IllegalArgumentException("URL must contain a valid host");
			}

		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid URL", e);
		}
	}

	private void validateExpiration(Instant expiresAt) {

		if (expiresAt != null && !expiresAt.isAfter(Instant.now())) {
			throw new IllegalArgumentException(
					"Expiration time must be in the future"
			);
		}
	}

	private Duration calculateCacheTtl(Instant expiresAt) {

		if (expiresAt == null) {
			return Duration.ofHours(1);
		}

		return Duration.between(
				Instant.now(),
				expiresAt
		);
	}
}