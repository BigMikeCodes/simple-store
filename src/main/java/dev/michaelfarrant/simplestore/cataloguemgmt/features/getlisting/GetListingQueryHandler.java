package dev.michaelfarrant.simplestore.cataloguemgmt.features.getlisting;

import dev.michaelfarrant.simplestore.cataloguemgmt.ListingStatus;
import dev.michaelfarrant.simplestore.cqrs.QueryHandler;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetListingQueryHandler implements QueryHandler<GetListingQuery, Optional<ListingProjection>> {

    private final JdbcClient jdbcClient;

    public GetListingQueryHandler(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ListingProjection> handleQuery(GetListingQuery query) {

        final String selectItems = """
            SELECT li.item_id, i.code, i.title, li.quantity
            FROM listing_item li
            JOIN item i ON i.id = li.item_id
            WHERE li.listing_id = :listingId
            ORDER BY i.title
            """;

        final String selectListing = """
            SELECT id, title, description, status, created, updated, published, withdrawn
            FROM listing
            WHERE id = :listingId
            """;

        List<ListingProjection.Item> items = jdbcClient.sql(selectItems).param("listingId", query.listingId())
                .query((rs, i) -> new ListingProjection.Item(
                        rs.getObject("item_id", UUID.class),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("quantity")))
                .list();

        return jdbcClient.sql(selectListing).param("listingId", query.listingId())
                .query((rs, i) -> {
                    ListingStatus status = ListingStatus.valueOf(rs.getString("status"));
                    return new ListingProjection(
                            rs.getObject("id", UUID.class),
                            rs.getString("title"),
                            rs.getString("description"),
                            status,
                            status == ListingStatus.DRAFT && !items.isEmpty(),
                            rs.getObject("created", OffsetDateTime.class),
                            rs.getObject("updated", OffsetDateTime.class),
                            rs.getObject("published", OffsetDateTime.class),
                            rs.getObject("withdrawn", OffsetDateTime.class),
                            items);
                }).optional();
    }
}
