package dev.michaelfarrant.simplestore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductDocument (UUID id, String name, String description, Set<String> tags) {
}
