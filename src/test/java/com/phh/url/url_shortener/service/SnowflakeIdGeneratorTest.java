package com.phh.url.url_shortener.service;

import com.phh.url.url_shortener.configuration.SnowflakeProperties;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SnowflakeIdGeneratorTest {

	@Test
	void shouldGenerateUniqueIds() {

		SnowflakeIdGenerator generator =
				new SnowflakeIdGenerator(
						new SnowflakeProperties(1)
				);

		long first = generator.nextId();
		long second = generator.nextId();
		long third = generator.nextId();

		assertNotEquals(first, second);
		assertNotEquals(second, third);
		assertNotEquals(first, third);
	}

	@Test
	void idsShouldBePositive() {

		SnowflakeIdGenerator generator =
				new SnowflakeIdGenerator(
						new SnowflakeProperties(1)
				);

		assertTrue(generator.nextId() > 0);
	}

	@Test
	void idsShouldBeIncreasing() {

		SnowflakeIdGenerator generator =
				new SnowflakeIdGenerator(
						new SnowflakeProperties(1)
				);

		long first = generator.nextId();
		long second = generator.nextId();
		long third = generator.nextId();

		assertTrue(first < second);
		assertTrue(second < third);
	}

	@Test
	void differentWorkersShouldGenerateUniqueIds() {

		SnowflakeIdGenerator worker1 =
				new SnowflakeIdGenerator(
						new SnowflakeProperties(1)
				);

		SnowflakeIdGenerator worker2 =
				new SnowflakeIdGenerator(
						new SnowflakeProperties(2)
				);

		Set<Long> ids = new HashSet<>();

		for (int i = 0; i < 10_000; i++) {
			ids.add(worker1.nextId());
			ids.add(worker2.nextId());
		}

		assertEquals(20_000, ids.size());
	}

	@Test
	void shouldRejectWorkerIdAboveMaximum() {

		assertThrows(
				IllegalArgumentException.class,
				() -> new SnowflakeIdGenerator(
						new SnowflakeProperties(1024)
				)
		);
	}

	@Test
	void shouldRejectNegativeWorkerId() {

		assertThrows(
				IllegalArgumentException.class,
				() -> new SnowflakeIdGenerator(
						new SnowflakeProperties(-1)
				)
		);
	}

	@Test
	void shouldGenerateUniqueIdsForSingleWorker() {

		SnowflakeIdGenerator generator =
				new SnowflakeIdGenerator(
						new SnowflakeProperties(1)
				);

		Set<Long> ids = new HashSet<>();

		for (int i = 0; i < 100_000; i++) {
			ids.add(generator.nextId());
		}

		assertEquals(100_000, ids.size());
	}
}