package dev.michaelfarrant.simplestore.storefront.catalogue;

import dev.michaelfarrant.simplestore.storefront.catalogue.byid.ProductByIdQuery;
import dev.michaelfarrant.simplestore.storefront.catalogue.byid.ProductByIdQueryHandler;
import dev.michaelfarrant.simplestore.storefront.catalogue.searchproducts.SearchProductsQuery;
import dev.michaelfarrant.simplestore.storefront.catalogue.searchproducts.SearchProductsQueryHandler;
import dev.michaelfarrant.simplestore.storefront.catalogue.searchproducts.SearchProductsResponse;
import dev.michaelfarrant.simplestore.storefront.catalogue.searchsuggestion.SearchSuggestionQuery;
import dev.michaelfarrant.simplestore.storefront.catalogue.searchsuggestion.SearchSuggestionQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private final ProductByIdQueryHandler productByIdQueryHandler;
    private final SearchSuggestionQueryHandler searchSuggestionQueryHandler;
    private final SearchProductsQueryHandler searchProductsQueryHandler;

    public ProductController(
            ProductByIdQueryHandler productByIdQueryHandler,
            SearchSuggestionQueryHandler searchSuggestionQueryHandler,
            SearchProductsQueryHandler searchProductsQueryHandler) {
        this.productByIdQueryHandler = productByIdQueryHandler;
        this.searchSuggestionQueryHandler = searchSuggestionQueryHandler;
        this.searchProductsQueryHandler = searchProductsQueryHandler;
    }

    @GetMapping("{productId}")
    public ResponseEntity<ProductDocument> getProductById(@PathVariable UUID productId) {

        ProductByIdQuery query = new ProductByIdQuery(productId);
        Optional<ProductDocument> document = productByIdQueryHandler.handleQuery(query);

        if(document.isPresent()){
            return ResponseEntity.ok(document.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("search-suggestions")
    public ResponseEntity<ProductSearchSuggestionResponse> searchSuggestion(@RequestParam(name = "searchTerm") String searchTerm){
        SearchSuggestionQuery query = new SearchSuggestionQuery(searchTerm);
        ProductSearchSuggestionResponse response = searchSuggestionQueryHandler.handleQuery(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("search")
    public ResponseEntity<SearchProductsResponse> searchProducts(
            @RequestParam(name = "searchTerm") String searchTerm,
            @RequestParam(name = "isSuggestion", required = false) Optional<Boolean> isSuggestion) {

        boolean isSuggestBool = isSuggestion.orElse(false);
        SearchProductsQuery query = new SearchProductsQuery(searchTerm, isSuggestBool, 10);

        SearchProductsResponse response = searchProductsQueryHandler.handleQuery(query);
        return ResponseEntity.ok(response);
    }

}
