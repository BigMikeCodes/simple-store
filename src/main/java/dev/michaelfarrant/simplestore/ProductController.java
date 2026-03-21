package dev.michaelfarrant.simplestore;

import dev.michaelfarrant.simplestore.command.create.CreateProductCommand;
import dev.michaelfarrant.simplestore.command.create.CreateProductCommandHandler;
import dev.michaelfarrant.simplestore.query.byid.ProductByIdQuery;
import dev.michaelfarrant.simplestore.query.byid.ProductByIdQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private final CreateProductCommandHandler createProductCommandHandler;
    private final ProductByIdQueryHandler productByIdQueryHandler;

    public ProductController(
            CreateProductCommandHandler createProductCommandHandler,
            ProductByIdQueryHandler productByIdQueryHandler) {
        this.createProductCommandHandler = createProductCommandHandler;
        this.productByIdQueryHandler = productByIdQueryHandler;
    }

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody CreateProductRequest request) {
        CreateProductCommand command = new CreateProductCommand(request.name(), request.description(), request.tags());
        UUID id = createProductCommandHandler.handleCommand(command);

        URI uri = linkTo(methodOn(ProductController.class).getProductById(id)).toUri();
        return ResponseEntity.created(uri).build();
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
}
