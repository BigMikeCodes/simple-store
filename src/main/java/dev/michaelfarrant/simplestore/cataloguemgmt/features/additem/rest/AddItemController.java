package dev.michaelfarrant.simplestore.cataloguemgmt.features.additem.rest;

import dev.michaelfarrant.simplestore.cataloguemgmt.features.additem.AddItemCommand;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.additem.AddItemCommandHandler;
import dev.michaelfarrant.simplestore.cataloguemgmt.rest.CatalogueManagementBaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AddItemController extends CatalogueManagementBaseController {

    private final AddItemCommandHandler commandHandler;

    public AddItemController(AddItemCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @PostMapping("items")
    public ResponseEntity<Void> addItem(AddItemRequest request) {
        AddItemCommand command = toCommand(request);
        UUID result = commandHandler.handleCommand(command);
        return ResponseEntity.ok().build();
    }

    private static AddItemCommand toCommand(AddItemRequest request) {
        return new AddItemCommand(request.title(), request.description());
    }

}
