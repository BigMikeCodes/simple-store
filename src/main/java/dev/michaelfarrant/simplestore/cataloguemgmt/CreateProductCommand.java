package dev.michaelfarrant.simplestore.cataloguemgmt;

import java.util.Set;

public record CreateProductCommand(String name, String description, Set<String> tags) {
}
