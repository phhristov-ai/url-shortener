package com.phh.url.url_shortener.controller;

import com.phh.url.url_shortener.cache.RateLimiter;
import com.phh.url.url_shortener.dto.CreateUrlRequest;
import com.phh.url.url_shortener.dto.CreateUrlResponse;
import com.phh.url.url_shortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlController {

	private final UrlService urlService;
	private final RateLimiter rateLimiter;

	@PostMapping("/api/urls")
	public CreateUrlResponse shortenUrl(
			@RequestBody @Valid CreateUrlRequest request,
			HttpServletRequest httpRequest
	) {
		String clientIp = httpRequest.getRemoteAddr();

		if (!rateLimiter.isAllowed(clientIp)) {
			throw new ResponseStatusException(
					HttpStatus.TOO_MANY_REQUESTS,
					"Rate limit exceeded"
			);
		}

		return urlService.shortenUrl(request);
	}

	@GetMapping("/{shortCode}")
	public ResponseEntity<Void> redirect(
			@PathVariable String shortCode
	) {

		String originalUrl =
				urlService.getOriginalUrl(shortCode);

		return ResponseEntity
				.status(302)
				.location(URI.create(originalUrl))
				.build();
	}
}