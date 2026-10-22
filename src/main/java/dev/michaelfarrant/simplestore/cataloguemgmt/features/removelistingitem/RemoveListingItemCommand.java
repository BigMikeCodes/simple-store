package dev.michaelfarrant.simplestore.cataloguemgmt.features.removelistingitem;

import dev.michaelfarrant.simplestore.cqrs.Command;

import java.util.UUID;

public record RemoveListingItemCommand(UUID listingId, UUID itemId) implements Command {
}
