package com.phh.url.url_shortener.controller;

import com.phh.url.url_shortener.dto.CreateUrlRequest;
import com.phh.url.url_shortener.dto.CreateUrlResponse;
import com.phh.url.url_shortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlController {

	private final UrlService urlService;

	@PostMapping("/api/urls")
	public ResponseEntity<CreateUrlResponse> shortenUrl(
			@Valid @RequestBody CreateUrlRequest request
	) {

		CreateUrlResponse response =
				urlService.shortenUrl(request);

		return ResponseEntity.ok(response);
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