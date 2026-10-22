package dev.michaelfarrant.simplestore.cataloguemgmt.features.updatelistingdetails.rest;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record UpdateListingDetailsRequest(
        @NotEmpty
        @Length(max = 200)
        String title,

        @NotEmpty
        String description
) {
}
