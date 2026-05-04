package dev.michaelfarrant.simplestore.cataloguemgmt.features.getitem;

import dev.michaelfarrant.simplestore.cqrs.QueryHandler;
import org.springframework.stereotype.Service;

@Service
public class GetItemQueryHandler implements QueryHandler<GetItemQuery, ItemProjection> {

    @Override
    public ItemProjection handleQuery(GetItemQuery query) {
        return null;
    }
}
