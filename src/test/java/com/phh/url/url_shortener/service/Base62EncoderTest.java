package com.phh.url.url_shortener.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Base62EncoderTest {

	private final Base62Encoder encoder = new Base62Encoder();

	@Test
	void shouldEncodeZero() {
		assertEquals("0", encoder.encode(0));
	}

	@Test
	void shouldEncodeSingleDigitValues() {
		assertEquals("0", encoder.encode(0));
		assertEquals("9", encoder.encode(9));
		assertEquals("a", encoder.encode(10));
		assertEquals("z", encoder.encode(35));
		assertEquals("A", encoder.encode(36));
		assertEquals("Z", encoder.encode(61));
	}

	@Test
	void shouldEncodeFirstValueOfSecondDigit() {
		assertEquals("10", encoder.encode(62));
	}

	@Test
	void shouldEncodeValuesAroundBaseBoundary() {
		assertEquals("Z", encoder.encode(61));
		assertEquals("10", encoder.encode(62));
		assertEquals("11", encoder.encode(63));
		assertEquals("1Z", encoder.encode(123));
		assertEquals("20", encoder.encode(124));
	}

	@Test
	void shouldDecodeSingleDigitValues() {
		assertEquals(0, encoder.decode("0"));
		assertEquals(9, encoder.decode("9"));
		assertEquals(10, encoder.decode("a"));
		assertEquals(35, encoder.decode("z"));
		assertEquals(36, encoder.decode("A"));
		assertEquals(61, encoder.decode("Z"));
	}

	@Test
	void shouldDecodeMultipleDigitValues() {
		assertEquals(62, encoder.decode("10"));
		assertEquals(63, encoder.decode("11"));
		assertEquals(123, encoder.decode("1Z"));
		assertEquals(124, encoder.decode("20"));
	}

	@Test
	void shouldRoundTripNumber() {

		long number = 123456789L;

		String encoded = encoder.encode(number);

		long decoded = encoder.decode(encoded);

		assertEquals(number, decoded);
	}

	@Test
	void shouldRoundTripSeveralNumbers() {

		long[] numbers = {
				0,
				1,
				9,
				10,
				35,
				36,
				61,
				62,
				63,
				1000,
				123456,
				123456789,
				Long.MAX_VALUE
		};

		for (long number : numbers) {

			String encoded = encoder.encode(number);

			assertEquals(
					number,
					encoder.decode(encoded)
			);
		}
	}

	@Test
	void shouldRejectNegativeNumber() {
		assertThrows(
				IllegalArgumentException.class,
				() -> encoder.encode(-1)
		);
	}

	@Test
	void shouldRejectEmptyValue() {
		assertThrows(
				IllegalArgumentException.class,
				() -> encoder.decode("")
		);
	}

	@Test
	void shouldRejectNullValue() {
		assertThrows(
				IllegalArgumentException.class,
				() -> encoder.decode(null)
		);
	}

	@Test
	void shouldRejectInvalidCharacter() {
		assertThrows(
				IllegalArgumentException.class,
				() -> encoder.decode("!")
		);
	}

	@Test
	void shouldRejectInvalidCharacterInMiddleOfValue() {
		assertThrows(
				IllegalArgumentException.class,
				() -> encoder.decode("ab!cd")
		);
	}
}