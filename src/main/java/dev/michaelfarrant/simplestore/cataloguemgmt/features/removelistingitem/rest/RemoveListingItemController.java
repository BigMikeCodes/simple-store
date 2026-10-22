package dev.michaelfarrant.simplestore.cataloguemgmt.features.removelistingitem.rest;

import dev.michaelfarrant.simplestore.cataloguemgmt.features.removelistingitem.RemoveListingItemCommand;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.removelistingitem.RemoveListingItemCommandHandler;
import dev.michaelfarrant.simplestore.cataloguemgmt.rest.CatalogueManagementBaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RemoveListingItemController extends CatalogueManagementBaseController {

    private final RemoveListingItemCommandHandler commandHandler;

    public RemoveListingItemController(RemoveListingItemCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @DeleteMapping("listings/{id}/items/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable UUID id, @PathVariable UUID itemId) {

        commandHandler.handleCommand(new RemoveListingItemCommand(id, itemId));
        return ResponseEntity.noContent().build();
    }

}
