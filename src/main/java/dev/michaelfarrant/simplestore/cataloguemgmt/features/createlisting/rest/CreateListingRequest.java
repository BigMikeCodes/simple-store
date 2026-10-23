package dev.michaelfarrant.simplestore.cataloguemgmt.features.createlisting.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.UUID;

public record CreateListingRequest(
        @NotEmpty
        @Length(max = 200)
        String title,

        @NotEmpty
        String description,

        // Optional: items can be added to the draft later
        List<@Valid @NotNull ListingItemRequest> items
) {

    public record ListingItemRequest(
            @NotNull
            UUID itemId,

            @NotNull
            @Positive
            Integer quantity
    ) {
    }
}
