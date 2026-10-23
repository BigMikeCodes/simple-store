package dev.michaelfarrant.simplestore.cataloguemgmt.features.getitem;

import java.util.UUID;

public record ItemProjection(
        UUID id,
        String title,
        String description,
        String code) {
}
