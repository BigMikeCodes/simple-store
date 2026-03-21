package dev.michaelfarrant.simplestore.config.opensearch;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchClientBeanConfig {

    @Bean
    public OpenSearchClient opensearchClient(OpenSearchConfiguration config) {
        final HttpHost httpHost = new HttpHost(config.scheme(), config.host(), config.port());

        // Configure Basic Auth for HttpClient 5
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(httpHost),
                new UsernamePasswordCredentials(config.username(), config.password().toCharArray())
        );

        // Build the transport using Apache HttpClient 5
        OpenSearchTransport transport = ApacheHttpClient5TransportBuilder
                .builder(httpHost)
                .setMapper(new JacksonJsonpMapper())
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                )
                .build();

        return new OpenSearchClient(transport);
    }

}
