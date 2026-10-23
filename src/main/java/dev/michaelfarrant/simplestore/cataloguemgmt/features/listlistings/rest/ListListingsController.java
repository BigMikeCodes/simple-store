package dev.michaelfarrant.simplestore.cataloguemgmt.features.listlistings.rest;

import dev.michaelfarrant.simplestore.cataloguemgmt.ListingStatus;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.listlistings.ListListingsQuery;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.listlistings.ListListingsQueryHandler;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.listlistings.ListingSummaryProjection;
import dev.michaelfarrant.simplestore.cataloguemgmt.rest.CatalogueManagementBaseController;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ListListingsController extends CatalogueManagementBaseController {

    private final ListListingsQueryHandler queryHandler;

    public ListListingsController(ListListingsQueryHandler queryHandler) {
        this.queryHandler = queryHandler;
    }

    @GetMapping("listings")
    public ResponseEntity<List<ListingSummaryProjection>> listListings(
            @RequestParam(name = "status", required = false) Optional<ListingStatus> status,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) int size) {

        ListListingsQuery query = new ListListingsQuery(status, page, size);
        return ResponseEntity.ok(queryHandler.handleQuery(query));
    }

}
