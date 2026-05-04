package dev.michaelfarrant.simplestore.storefront.catalogue.searchproducts;

import dev.michaelfarrant.simplestore.storefront.catalogue.ProductDocument;

import java.util.List;

public record SearchProductsResponse(
        String searchTerm,
        List<ProductDocument> results
) {

    public SearchProductsResponse(String searchTerm) {
        this(searchTerm, List.of());
    }
}
