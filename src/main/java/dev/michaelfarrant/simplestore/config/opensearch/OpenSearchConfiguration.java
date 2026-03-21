package dev.michaelfarrant.simplestore.config.opensearch;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "opensearch")
public record OpenSearchConfiguration(
        String host,
        int port,
        String scheme,
        String username,
        String password
) {
}
