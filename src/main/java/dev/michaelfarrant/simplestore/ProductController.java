package dev.michaelfarrant.simplestore;

import dev.michaelfarrant.simplestore.command.create.CreateProductCommand;
import dev.michaelfarrant.simplestore.command.create.CreateProductCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private final CreateProductCommandHandler createProductCommandHandler;

    public ProductController(CreateProductCommandHandler createProductCommandHandler) {
        this.createProductCommandHandler = createProductCommandHandler;
    }

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody CreateProductRequest request) {
        CreateProductCommand command = new CreateProductCommand(request.name(), request.description(), request.tags());
        createProductCommandHandler.handleCommand(command);
        return ResponseEntity.ok().build();
    }
}
