package com.phh.url.url_shortener.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "snowflake")
public record SnowflakeProperties(long workerId) {
}