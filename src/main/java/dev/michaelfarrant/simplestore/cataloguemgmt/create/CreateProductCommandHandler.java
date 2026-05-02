package dev.michaelfarrant.simplestore.cataloguemgmt.create;

import dev.michaelfarrant.simplestore.storefront.catalogue.Indexes;
import dev.michaelfarrant.simplestore.storefront.catalogue.ProductDocument;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class CreateProductCommandHandler {

    private final OpenSearchClient openSearchClient;

    public CreateProductCommandHandler(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    public UUID handleCommand(CreateProductCommand command) {

        ProductDocument document = toDocument(command);
        IndexRequest<ProductDocument> request = toIndexRequest(document);

        try {
            openSearchClient.index(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return document.id();
    }

    private static ProductDocument toDocument(CreateProductCommand command){
        return new ProductDocument(UUID.randomUUID(), command.name(), command.description(), command.tags());
    }

    private static IndexRequest<ProductDocument> toIndexRequest(ProductDocument document){
        return new IndexRequest.Builder<ProductDocument>()
                .index(Indexes.PRODUCTS)
                .id(document.id().toString())
                .document(document)
                .build();
    }

}
