package com.phh.url.url_shortener.kafka;

import java.time.Instant;

public record UrlClickedEvent(
		String shortCode,
		Instant clickedAt
) {
}