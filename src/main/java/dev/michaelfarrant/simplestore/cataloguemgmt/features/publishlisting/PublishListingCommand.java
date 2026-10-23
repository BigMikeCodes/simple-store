package dev.michaelfarrant.simplestore.cataloguemgmt.features.publishlisting;

import dev.michaelfarrant.simplestore.cqrs.Command;

import java.util.UUID;

public record PublishListingCommand(UUID listingId) implements Command {
}
