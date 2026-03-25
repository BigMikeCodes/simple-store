package dev.michaelfarrant.simplestore;

import java.util.List;

public record ProductSearchSuggestionResponse(String searchTerm, List<String> suggestions) {
}
