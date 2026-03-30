package dev.michaelfarrant.simplestore.query.searchproducts;

import dev.michaelfarrant.simplestore.ProductDocument;

import java.util.List;

public record SearchProductsResponse(
        String searchTerm,
        List<ProductDocument> results
) {

    public SearchProductsResponse(String searchTerm) {
        this(searchTerm, List.of());
    }
}
