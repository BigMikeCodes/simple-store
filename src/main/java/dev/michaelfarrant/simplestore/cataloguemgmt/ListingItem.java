package dev.michaelfarrant.simplestore.cataloguemgmt;

import java.util.Objects;
import java.util.UUID;

public record ListingItem(UUID itemId, int quantity) {

    public ListingItem {
        Objects.requireNonNull(itemId, "itemId");
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be at least 1");
        }
    }

}
