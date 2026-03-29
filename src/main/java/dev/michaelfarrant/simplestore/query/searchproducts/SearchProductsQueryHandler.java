package dev.michaelfarrant.simplestore.query.searchproducts;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.stereotype.Service;

@Service
public class SearchProductsQueryHandler {

    private final OpenSearchClient openSearchClient;

    public SearchProductsQueryHandler(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    public void handleQuery(SearchProductsQuery query){




    }

}
