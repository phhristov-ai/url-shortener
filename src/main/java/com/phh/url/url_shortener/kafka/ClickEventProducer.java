package com.phh.url.url_shortener.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClickEventProducer {

	private static final String TOPIC = "url-clicked";

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void send(UrlClickedEvent event) {

		kafkaTemplate.send(
				TOPIC,
				event.shortCode(),
				event
		);
	}
}