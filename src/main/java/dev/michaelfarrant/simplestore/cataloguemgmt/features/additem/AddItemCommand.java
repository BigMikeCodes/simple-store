package dev.michaelfarrant.simplestore.cataloguemgmt.features.additem;

import dev.michaelfarrant.simplestore.cqrs.Command;

public record AddItemCommand(String title, String description) implements Command {
}
