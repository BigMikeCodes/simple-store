package dev.michaelfarrant.simplestore.cataloguemgmt.features.createlisting;

import dev.michaelfarrant.simplestore.cqrs.Command;

import java.util.List;

public record CreateListingFromItemCommand (List<ItemQuantity> items) implements Command {}
