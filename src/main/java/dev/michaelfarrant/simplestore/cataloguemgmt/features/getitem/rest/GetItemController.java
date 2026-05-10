package dev.michaelfarrant.simplestore.cataloguemgmt.features.getitem.rest;

import dev.michaelfarrant.simplestore.cataloguemgmt.features.getitem.GetItemQuery;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.getitem.GetItemQueryHandler;
import dev.michaelfarrant.simplestore.cataloguemgmt.features.getitem.ItemProjection;
import dev.michaelfarrant.simplestore.cataloguemgmt.rest.CatalogueManagementBaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
public class GetItemController extends CatalogueManagementBaseController {

    private final GetItemQueryHandler queryHandler;

    public GetItemController(GetItemQueryHandler queryHandler) {
        this.queryHandler = queryHandler;
    }

    @GetMapping("items/{id}")
    public ResponseEntity<ItemProjection> getItem(@PathVariable UUID id){

        GetItemQuery query = new GetItemQuery(id);
        Optional<ItemProjection> result = queryHandler.handleQuery(query);

        return result.isPresent()
                ? ResponseEntity.ok(result.get())
                : ResponseEntity.notFound().build();
    }

}
