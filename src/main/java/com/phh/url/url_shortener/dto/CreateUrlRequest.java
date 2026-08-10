package com.phh.url.url_shortener.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record CreateUrlRequest(
		@NotBlank
		String url,

		Instant expiresAt
) {
}