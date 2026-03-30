package dev.michaelfarrant.simplestore.query.searchproducts;

import dev.michaelfarrant.simplestore.Indexes;
import dev.michaelfarrant.simplestore.ProductDocument;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SearchProductsQueryHandler {

    private static final Logger logger = LoggerFactory.getLogger(SearchProductsQueryHandler.class);

    private final OpenSearchClient openSearchClient;

    public SearchProductsQueryHandler(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    public SearchProductsResponse handleQuery(SearchProductsQuery query) {

        SearchRequest request = new SearchRequest.Builder()
                .index(Indexes.PRODUCTS)
                .size(query.pageSize())
                .query(q -> q.multiMatch(
                        m -> m.fields("name", "description", "tags")
                                .query(query.term())))
                .build();

        try {
            SearchResponse<ProductDocument> response = openSearchClient.search(request, ProductDocument.class);
            return new SearchProductsResponse(query.term(), response.hits().hits().stream().map(hit -> hit.source()).toList());
        }
        catch(Exception exception) {
            logger.error("Error searching products", exception);
        }

        return new SearchProductsResponse(query.term());
    }

}
