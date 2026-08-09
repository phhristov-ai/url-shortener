package com.phh.url.url_shortener.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUrlRequest(

		@NotBlank
		String url
) {
}