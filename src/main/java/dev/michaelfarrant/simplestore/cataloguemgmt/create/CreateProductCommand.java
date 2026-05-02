package dev.michaelfarrant.simplestore.cataloguemgmt.create;

import java.util.Set;

public record CreateProductCommand(String name, String description, Set<String> tags) {
}
