package dev.michaelfarrant.simplestore.cataloguemgmt.features.setlistingitem;

import dev.michaelfarrant.simplestore.cqrs.Command;

import java.util.UUID;

public record SetListingItemCommand(UUID listingId, UUID itemId, int quantity) implements Command {
}
