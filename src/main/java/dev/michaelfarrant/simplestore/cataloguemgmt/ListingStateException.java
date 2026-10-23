package dev.michaelfarrant.simplestore.cataloguemgmt;

/**
 * Thrown when an operation isn't permitted for a listing in its current {@link ListingStatus}.
 */
public class ListingStateException extends RuntimeException {

    public ListingStateException(String message) {
        super(message);
    }

}
