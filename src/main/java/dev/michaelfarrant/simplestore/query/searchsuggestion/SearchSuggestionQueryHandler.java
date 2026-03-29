package dev.michaelfarrant.simplestore.query.searchsuggestion;

import dev.michaelfarrant.simplestore.Indexes;
import dev.michaelfarrant.simplestore.ProductDocument;
import dev.michaelfarrant.simplestore.ProductSearchSuggestionResponse;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;
import org.opensearch.client.opensearch._types.query_dsl.TextQueryType;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SearchSuggestionQueryHandler {

    private static final Logger logger = LoggerFactory.getLogger(SearchSuggestionQueryHandler.class);

    private static final int MAX_SUGGESTIONS = 10;
    private static final String SOURCE = "v1-search-suggestion";
    private static final String OPENSEARCH_AGGREGATION = "unique_suggestions";

    private final OpenSearchClient openSearchClient;

    public SearchSuggestionQueryHandler(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    public ProductSearchSuggestionResponse handleQuery(SearchSuggestionQuery query) {

        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(Indexes.PRODUCTS)
                .size(0)
                .source(src -> src
                        .filter(f -> f
                                .includes("name")
                        )
                )
                .query(q -> q
                        .multiMatch(m -> m
                                .query(query.searchTerm()) // e.g., "dri"
                                .type(TextQueryType.BoolPrefix)
                                .fields("name", "name._2gram", "name._3gram")
                        )
                )
                .aggregations(OPENSEARCH_AGGREGATION, a -> a
                        .terms(t -> t
                                .field("name.raw")
                                .size(MAX_SUGGESTIONS)
                        )
                )
                .build();

        SearchResponse<Void> response = null;
        try {
            response = openSearchClient.search(searchRequest, Void.class);

        } catch (IOException e) {
            // Log and return empty response.
            logger.error("Error executing search suggestion query", e);
            return new ProductSearchSuggestionResponse(query.searchTerm(), List.of(), SOURCE);
        }

        List<String> suggestions = response.aggregations().get(OPENSEARCH_AGGREGATION).sterms().buckets().array().stream().map(StringTermsBucket::key).toList();
        return new ProductSearchSuggestionResponse(query.searchTerm(), suggestions, SOURCE);
    }

}
