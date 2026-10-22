package dev.michaelfarrant.simplestore.cataloguemgmt.features.withdrawlisting;

import dev.michaelfarrant.simplestore.cqrs.Command;

import java.util.UUID;

public record WithdrawListingCommand(UUID listingId) implements Command {
}
