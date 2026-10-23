package dev.michaelfarrant.simplestore.cataloguemgmt.features.getitem;

import dev.michaelfarrant.simplestore.cqrs.Query;

import java.util.UUID;

public record GetItemQuery(UUID itemId) implements Query {
}
