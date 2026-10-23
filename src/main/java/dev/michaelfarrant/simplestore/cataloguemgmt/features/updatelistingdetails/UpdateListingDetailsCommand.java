package dev.michaelfarrant.simplestore.cataloguemgmt.features.updatelistingdetails;

import dev.michaelfarrant.simplestore.cqrs.Command;

import java.util.UUID;

public record UpdateListingDetailsCommand(UUID listingId, String title, String description) implements Command {
}
