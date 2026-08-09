package com.phh.url.url_shortener.repository;

import com.phh.url.url_shortener.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<ShortUrl, Long> {

	Optional<ShortUrl> findByShortCode(String shortCode);
}