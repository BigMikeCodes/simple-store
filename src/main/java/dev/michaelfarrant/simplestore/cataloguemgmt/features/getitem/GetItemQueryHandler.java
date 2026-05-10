package dev.michaelfarrant.simplestore.cataloguemgmt.features.getitem;

import dev.michaelfarrant.simplestore.cqrs.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetItemQueryHandler implements QueryHandler<GetItemQuery, Optional<ItemProjection>> {

    @Override
    public Optional<ItemProjection> handleQuery(GetItemQuery query) {
        return Optional.empty();
    }
}
