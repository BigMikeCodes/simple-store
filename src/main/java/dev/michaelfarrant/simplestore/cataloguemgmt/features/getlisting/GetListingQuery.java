package dev.michaelfarrant.simplestore.cataloguemgmt.features.getlisting;

import dev.michaelfarrant.simplestore.cqrs.Query;

import java.util.UUID;

public record GetListingQuery(UUID listingId) implements Query {
}
