package dev.michaelfarrant.simplestore.query.searchproducts;

public record SearchProductsQuery(
        String term,
        boolean isSuggestion,
        int pageSize
){}
