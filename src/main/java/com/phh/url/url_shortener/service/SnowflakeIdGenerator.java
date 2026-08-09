package com.phh.url.url_shortener.service;

import com.phh.url.url_shortener.configuration.SnowflakeProperties;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdGenerator {

	private static final long EPOCH = 1577836800000L; // 2020-01-01

	private static final long WORKER_ID_BITS = 10;
	private static final long SEQUENCE_BITS = 12;

	private static final long MAX_WORKER_ID =
			(1L << WORKER_ID_BITS) - 1;

	private static final long MAX_SEQUENCE =
			(1L << SEQUENCE_BITS) - 1;

	private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

	private static final long TIMESTAMP_SHIFT =
			SEQUENCE_BITS + WORKER_ID_BITS;

	private final long workerId;

	private long sequence = 0;
	private long lastTimestamp = -1;

	public SnowflakeIdGenerator(SnowflakeProperties properties) {

		this.workerId = properties.workerId();

		if (workerId < 0 || workerId > MAX_WORKER_ID) {
			throw new IllegalArgumentException(
					"Worker ID must be between 0 and " + MAX_WORKER_ID
			);
		}
	}

	public synchronized long nextId() {

		long currentTimestamp = currentTimestamp();

		if (currentTimestamp < lastTimestamp) {
			throw new IllegalStateException(
					"Clock moved backwards"
			);
		}

		if (currentTimestamp == lastTimestamp) {

			sequence = (sequence + 1) & MAX_SEQUENCE;

			if (sequence == 0) {
				currentTimestamp = waitUntilNextMillis(
						lastTimestamp
				);
			}

		} else {
			sequence = 0;
		}

		lastTimestamp = currentTimestamp;

		return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)
				| (workerId << WORKER_ID_SHIFT)
				| sequence;
	}

	private long currentTimestamp() {
		return System.currentTimeMillis();
	}

	private long waitUntilNextMillis(long lastTimestamp) {

		long timestamp = currentTimestamp();

		while (timestamp <= lastTimestamp) {
			timestamp = currentTimestamp();
		}

		return timestamp;
	}
}