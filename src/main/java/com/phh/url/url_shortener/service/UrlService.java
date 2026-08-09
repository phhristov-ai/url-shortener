package com.phh.url.url_shortener.service;

import com.phh.url.url_shortener.dto.CreateUrlRequest;
import com.phh.url.url_shortener.dto.CreateUrlResponse;
import com.phh.url.url_shortener.entity.ShortUrl;
import com.phh.url.url_shortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class UrlService {

	private final UrlRepository urlRepository;
	private final Base62Encoder base62Encoder;
	private final SnowflakeIdGenerator snowflakeIdGenerator;

	@Transactional
	public CreateUrlResponse shortenUrl(CreateUrlRequest request) {

		validateUrl(request.url());

		long id = snowflakeIdGenerator.nextId();

		String shortCode = base62Encoder.encode(id);

		ShortUrl shortUrl = new ShortUrl(
				id,
				shortCode,
				request.url()
		);

		urlRepository.save(shortUrl);

		return new CreateUrlResponse(
				"http://localhost:8080/" + shortCode
		);
	}

	@Transactional(readOnly = true)
	public String getOriginalUrl(String shortCode) {

		ShortUrl shortUrl = urlRepository.findByShortCode(shortCode)
				.orElseThrow(() ->
						new IllegalArgumentException("Short URL not found")
				);

		return shortUrl.getOriginalUrl();
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
}