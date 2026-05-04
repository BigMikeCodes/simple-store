package dev.michaelfarrant.simplestore.storefront.catalogue;

import java.util.List;

public record ProductSearchSuggestionResponse(String searchTerm, List<String> suggestions, String source) {
}
