package com.phh.url.url_shortener.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "short_urls")
@Getter
@Setter
@NoArgsConstructor
public class ShortUrl {

	@Id
	private Long id;

	@Column(nullable = false, unique = true)
	private String shortCode;

	@Column(nullable = false, length = 2048)
	private String originalUrl;

	public ShortUrl(
			Long id,
			String shortCode,
			String originalUrl
	) {
		this.id = id;
		this.shortCode = shortCode;
		this.originalUrl = originalUrl;
	}
}