package dev.michaelfarrant.simplestore.cataloguemgmt.features.createlisting.rest;

import dev.michaelfarrant.simplestore.cataloguemgmt.features.createlisting.CreateListingCommand;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.createlisting.CreateListingCommandHandler;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.createlisting.ItemQuantity;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.getlisting.rest.GetListingController;
import dev.michaelfarrant.simplestore.cataloguemgmt.rest.CatalogueManagementBaseController;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CreateListingController extends CatalogueManagementBaseController {

    private final CreateListingCommandHandler commandHandler;

    public CreateListingController(CreateListingCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @PostMapping("listings")
    public ResponseEntity<Void> createListing(
            @Valid
            @RequestBody
            CreateListingRequest request) {

        CreateListingCommand command = toCommand(request);
        UUID listingId = commandHandler.handleCommand(command);

        URI uri = linkTo(methodOn(GetListingController.class).getListing(listingId)).toUri();
        return ResponseEntity.created(uri).build();
    }

    private static CreateListingCommand toCommand(CreateListingRequest request) {
        List<ItemQuantity> items = request.items() == null
                ? List.of()
                : request.items().stream().map(item -> new ItemQuantity(item.itemId(), item.quantity())).toList();
        return new CreateListingCommand(request.title(), request.description(), items);
    }

}
