package dev.michaelfarrant.simplestore.cataloguemgmt.features.listingfromitem;

import dev.michaelfarrant.simplestore.cqrs.Command;

import java.util.UUID;

public record CreateListingFromItemCommand (UUID itemId) implements Command {}
