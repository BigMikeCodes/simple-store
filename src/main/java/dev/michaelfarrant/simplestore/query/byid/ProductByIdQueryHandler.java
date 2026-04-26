package dev.michaelfarrant.simplestore.query.byid;

import dev.michaelfarrant.simplestore.Indexes;
import dev.michaelfarrant.simplestore.ProductDocument;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.GetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductByIdQueryHandler {

    private final Logger logger = LoggerFactory.getLogger(ProductByIdQueryHandler.class);
    private final OpenSearchClient openSearchClient;

    public ProductByIdQueryHandler(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    public Optional<ProductDocument> handleQuery(ProductByIdQuery query) {

        try {
            GetResponse<ProductDocument>  response = get(query.id());
            return Optional.of(response.source());

        } catch (IOException exception) {
            logger.error("Error fetching product", exception);
            return Optional.empty();
        }
    }

    private GetResponse<ProductDocument> get(UUID id) throws IOException {
        return openSearchClient.get(builder -> builder.index(Indexes.PRODUCTS).id(id.toString()), ProductDocument.class);
    }

}
