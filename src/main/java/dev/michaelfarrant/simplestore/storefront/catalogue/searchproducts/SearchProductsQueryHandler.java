package dev.michaelfarrant.simplestore.storefront.catalogue.searchproducts;

import dev.michaelfarrant.simplestore.storefront.catalogue.Indexes;
import dev.michaelfarrant.simplestore.storefront.catalogue.ProductDocument;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.MultiMatchQuery;
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

        MultiMatchQuery multiMatch = MultiMatchQuery.of(m -> m
                .fields("name^3", "name._2gram", "name._3gram","description", "tags")
                .fuzziness("AUTO")
                .query(query.term()));

        SearchRequest request = new SearchRequest.Builder()
                .index(Indexes.PRODUCTS)
                .size(query.pageSize())
                .query(multiMatch.toQuery())
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
