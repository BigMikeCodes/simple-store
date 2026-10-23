package dev.michaelfarrant.simplestore.cataloguemgmt.features.listlistings;

import dev.michaelfarrant.simplestore.cataloguemgmt.ListingStatus;
import dev.michaelfarrant.simplestore.cqrs.Query;

import java.util.Optional;

/**
 * @param status optional status filter
 * @param page   zero based page number
 * @param size   page size
 */
public record ListListingsQuery(Optional<ListingStatus> status, int page, int size) implements Query {
}
