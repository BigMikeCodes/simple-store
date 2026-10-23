package dev.michaelfarrant.simplestore.cataloguemgmt.features.listlistings;

import dev.michaelfarrant.simplestore.cataloguemgmt.ListingStatus;
import dev.michaelfarrant.simplestore.cqrs.QueryHandler;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ListListingsQueryHandler implements QueryHandler<ListListingsQuery, List<ListingSummaryProjection>> {

    private final JdbcClient jdbcClient;

    public ListListingsQueryHandler(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<ListingSummaryProjection> handleQuery(ListListingsQuery query) {

        final String selectListings = """
            SELECT l.id, l.title, l.status, l.created, l.updated,
                   (SELECT count(*) FROM listing_item li WHERE li.listing_id = l.id) AS item_count
            FROM listing l
            WHERE CAST(:status AS VARCHAR) IS NULL OR l.status = :status
            ORDER BY l.created DESC, l.id
            LIMIT :limit OFFSET :offset
            """;

        return jdbcClient.sql(selectListings)
                .param("status", query.status().map(Enum::name).orElse(null))
                .param("limit", query.size())
                .param("offset", (long) query.page() * query.size())
                .query((rs, i) -> new ListingSummaryProjection(
                        rs.getObject("id", UUID.class),
                        rs.getString("title"),
                        ListingStatus.valueOf(rs.getString("status")),
                        rs.getInt("item_count"),
                        rs.getObject("created", OffsetDateTime.class),
                        rs.getObject("updated", OffsetDateTime.class)))
                .list();
    }
}
