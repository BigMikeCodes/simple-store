package dev.michaelfarrant.simplestore.cataloguemgmt.features.additem.rest;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record AddItemRequest(
        @NotEmpty
        @Length(max = 200)
        String title,

        @NotEmpty
        String description,

        @NotEmpty
        @Length(max = 20)
        String code
) {
}
