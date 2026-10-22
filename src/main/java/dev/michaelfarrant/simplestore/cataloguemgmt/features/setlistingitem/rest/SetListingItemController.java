package dev.michaelfarrant.simplestore.cataloguemgmt.features.setlistingitem.rest;

import dev.michaelfarrant.simplestore.cataloguemgmt.features.setlistingitem.SetListingItemCommand;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.setlistingitem.SetListingItemCommandHandler;
import dev.michaelfarrant.simplestore.cataloguemgmt.rest.CatalogueManagementBaseController;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class SetListingItemController extends CatalogueManagementBaseController {

    private final SetListingItemCommandHandler commandHandler;

    public SetListingItemController(SetListingItemCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @PutMapping("listings/{id}/items/{itemId}")
    public ResponseEntity<Void> setItem(
            @PathVariable UUID id,
            @PathVariable UUID itemId,
            @Valid
            @RequestBody
            SetListingItemRequest request) {

        commandHandler.handleCommand(new SetListingItemCommand(id, itemId, request.quantity()));
        return ResponseEntity.noContent().build();
    }

}
