package dev.michaelfarrant.simplestore.cataloguemgmt.features.withdrawlisting.rest;

import dev.michaelfarrant.simplestore.cataloguemgmt.features.withdrawlisting.WithdrawListingCommand;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.withdrawlisting.WithdrawListingCommandHandler;
import dev.michaelfarrant.simplestore.cataloguemgmt.rest.CatalogueManagementBaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class WithdrawListingController extends CatalogueManagementBaseController {

    private final WithdrawListingCommandHandler commandHandler;

    public WithdrawListingController(WithdrawListingCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * Returns 202 as removal from the storefront happens asynchronously.
     */
    @PostMapping("listings/{id}/withdraw")
    public ResponseEntity<Void> withdraw(@PathVariable UUID id) {

        commandHandler.handleCommand(new WithdrawListingCommand(id));
        return ResponseEntity.accepted().build();
    }

}
