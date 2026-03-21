package dev.michaelfarrant.simplestore.query.byid;

import dev.michaelfarrant.simplestore.Indexes;
import dev.michaelfarrant.simplestore.ProductDocument;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.GetResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductByIdQueryHandler {

    private final OpenSearchClient openSearchClient;

    public ProductByIdQueryHandler(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    public Optional<ProductDocument> handleQuery(ProductByIdQuery query) {

        try {
            GetResponse<ProductDocument>  response = get(query.id());
            return Optional.of(response.source());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private GetResponse<ProductDocument> get(UUID id) throws IOException {
        return openSearchClient.get(builder -> builder.index(Indexes.PRODUCTS).id(id.toString()), ProductDocument.class);
    }

}
