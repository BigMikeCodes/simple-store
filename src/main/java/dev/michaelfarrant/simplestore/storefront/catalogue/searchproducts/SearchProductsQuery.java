package dev.michaelfarrant.simplestore.storefront.catalogue.searchproducts;

public record SearchProductsQuery(
        String term,
        boolean isSuggestion,
        int pageSize
){}
