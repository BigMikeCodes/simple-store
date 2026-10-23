package dev.michaelfarrant.simplestore.cataloguemgmt.features.getlisting.rest;

import dev.michaelfarrant.simplestore.cataloguemgmt.features.getlisting.GetListingQuery;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.getlisting.GetListingQueryHandler;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.getlisting.ListingProjection;
import dev.michaelfarrant.simplestore.cataloguemgmt.rest.CatalogueManagementBaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class GetListingController extends CatalogueManagementBaseController {

    private final GetListingQueryHandler queryHandler;

    public GetListingController(GetListingQueryHandler queryHandler) {
        this.queryHandler = queryHandler;
    }

    @GetMapping("listings/{id}")
    public ResponseEntity<ListingProjection> getListing(@PathVariable UUID id) {

        GetListingQuery query = new GetListingQuery(id);
        return ResponseEntity.of(queryHandler.handleQuery(query));
    }

}
