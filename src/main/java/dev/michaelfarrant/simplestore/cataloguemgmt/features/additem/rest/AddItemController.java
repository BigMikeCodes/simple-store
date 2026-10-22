package dev.michaelfarrant.simplestore.cataloguemgmt.features.additem.rest;

import dev.michaelfarrant.simplestore.cataloguemgmt.features.additem.AddItemCommand;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.additem.AddItemCommandHandler;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.getitem.rest.GetItemController;
import dev.michaelfarrant.simplestore.cataloguemgmt.rest.CatalogueManagementBaseController;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class AddItemController extends CatalogueManagementBaseController {

    private final AddItemCommandHandler commandHandler;

    public AddItemController(AddItemCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @PostMapping("items")
    public ResponseEntity<Void> addItem(
            @Valid
            @RequestBody
            AddItemRequest request) {

        AddItemCommand command = toCommand(request);
        UUID itemId = commandHandler.handleCommand(command);

        URI uri = linkTo(methodOn(GetItemController.class).getItem(itemId)).toUri();
        return ResponseEntity.created(uri).build();
    }

    private static AddItemCommand toCommand(AddItemRequest request) {
        return new AddItemCommand(request.title(), request.description(), request.code());
    }

}
