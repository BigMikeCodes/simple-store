package dev.michaelfarrant.simplestore.cataloguemgmt.features.setlistingitem.rest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SetListingItemRequest(
        @NotNull
        @Positive
        Integer quantity
) {
}
