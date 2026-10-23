package dev.michaelfarrant.simplestore.cataloguemgmt.features.updatelistingdetails.rest;

import dev.michaelfarrant.simplestore.cataloguemgmt.features.updatelistingdetails.UpdateListingDetailsCommand;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.updatelistingdetails.UpdateListingDetailsCommandHandler;
import dev.michaelfarrant.simplestore.cataloguemgmt.rest.CatalogueManagementBaseController;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UpdateListingDetailsController extends CatalogueManagementBaseController {

    private final UpdateListingDetailsCommandHandler commandHandler;

    public UpdateListingDetailsController(UpdateListingDetailsCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @PutMapping("listings/{id}/details")
    public ResponseEntity<Void> updateDetails(
            @PathVariable UUID id,
            @Valid
            @RequestBody
            UpdateListingDetailsRequest request) {

        commandHandler.handleCommand(new UpdateListingDetailsCommand(id, request.title(), request.description()));
        return ResponseEntity.noContent().build();
    }

}
