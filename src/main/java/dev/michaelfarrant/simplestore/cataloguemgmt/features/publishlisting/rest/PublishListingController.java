package dev.michaelfarrant.simplestore.cataloguemgmt.features.publishlisting.rest;

import dev.michaelfarrant.simplestore.cataloguemgmt.features.publishlisting.PublishListingCommand;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.publishlisting.PublishListingCommandHandler;
import dev.michaelfarrant.simplestore.cataloguemgmt.rest.CatalogueManagementBaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class PublishListingController extends CatalogueManagementBaseController {

    private final PublishListingCommandHandler commandHandler;

    public PublishListingController(PublishListingCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * Returns 202 as the listing appears in the storefront asynchronously.
     */
    @PostMapping("listings/{id}/publish")
    public ResponseEntity<Void> publish(@PathVariable UUID id) {

        commandHandler.handleCommand(new PublishListingCommand(id));
        return ResponseEntity.accepted().build();
    }

}
