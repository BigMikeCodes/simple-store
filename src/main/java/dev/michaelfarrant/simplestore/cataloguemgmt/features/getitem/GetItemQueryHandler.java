package dev.michaelfarrant.simplestore.cataloguemgmt.features.getitem;

import dev.michaelfarrant.simplestore.cqrs.QueryHandler;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetItemQueryHandler implements QueryHandler<GetItemQuery, Optional<ItemProjection>> {

    private final JdbcClient jdbcClient;

    public GetItemQueryHandler(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<ItemProjection> handleQuery(GetItemQuery query) {

        final String selectItem = """
            SELECT id, title, description, code
            FROM item
            WHERE id = :itemId
            """;

        return jdbcClient.sql(selectItem).param("itemId", query.itemId())
                .query((rs, i) -> {
                    return new ItemProjection(
                            rs.getObject(1, java.util.UUID.class),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4)
                    );
                }).optional();
    }
}
