package com.phh.url.url_shortener.service;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

	private static final String CHARACTERS =
			"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static final int BASE = CHARACTERS.length();

	public String encode(long number) {

		if (number < 0) {
			throw new IllegalArgumentException("Number must be non-negative");
		}

		if (number == 0) {
			return String.valueOf(CHARACTERS.charAt(0));
		}

		StringBuilder result = new StringBuilder();

		while (number > 0) {
			int remainder = (int) (number % BASE);

			result.append(CHARACTERS.charAt(remainder));

			number /= BASE;
		}

		return result.reverse().toString();
	}

	public long decode(String value) {

		if (value == null || value.isEmpty()) {
			throw new IllegalArgumentException("Value must not be empty");
		}

		long result = 0;

		for (char character : value.toCharArray()) {

			int digit = CHARACTERS.indexOf(character);

			if (digit == -1) {
				throw new IllegalArgumentException(
						"Invalid Base62 character: " + character
				);
			}

			result = result * BASE + digit;
		}

		return result;
	}
}