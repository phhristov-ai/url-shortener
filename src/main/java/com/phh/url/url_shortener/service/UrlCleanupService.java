package com.phh.url.url_shortener.service;

import com.phh.url.url_shortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlCleanupService {

	private final UrlRepository urlRepository;

	@Scheduled(fixedRate = 60_000)
	@Transactional
	public void deleteExpiredUrls() {

		int deleted = urlRepository.deleteExpiredUrls(Instant.now());

		log.info("Expired URL cleanup completed. Deleted {} URLs", deleted);
	}
}