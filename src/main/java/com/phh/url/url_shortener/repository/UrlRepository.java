package com.phh.url.url_shortener.repository;

import com.phh.url.url_shortener.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<ShortUrl, Long> {

	Optional<ShortUrl> findByShortCode(String shortCode);

	@Modifying
	@Query("""
    DELETE FROM ShortUrl s
    WHERE s.expiresAt IS NOT NULL
      AND s.expiresAt < :now
""")
	int deleteExpiredUrls(@Param("now") Instant now);
}