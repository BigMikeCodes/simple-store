package dev.michaelfarrant.simplestore.cataloguemgmt;

import java.util.UUID;

public class ListingNotFoundException extends RuntimeException {

    public ListingNotFoundException(UUID listingId) {
        super("Listing %s not found".formatted(listingId));
    }

}
