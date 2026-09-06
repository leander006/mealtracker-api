package com.mealtracker.meal_tracker_api.inference.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class ExternalClientsConfig {

    @Bean
    public RestClient mlServiceRestClient(@Value("${ml.service.base-url}") String baseUrl) {
        // uvicorn (ml-service's server) only speaks HTTP/1.1. Spring's
        // default JDK HttpClient tries to negotiate HTTP/2 first via an
        // upgrade request, which uvicorn doesn't understand - it logs
        // "Unsupported upgrade request" and mishandles the multipart body
        // that follows, causing FastAPI to see the request as missing the
        // image field entirely even though it was genuinely sent.
        // Forcing HTTP/1.1 explicitly avoids the upgrade attempt.
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    public RestClient geminiRestClient(@Value("${gemini.api.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient usdaRestClient(@Value("${usda.api.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
 