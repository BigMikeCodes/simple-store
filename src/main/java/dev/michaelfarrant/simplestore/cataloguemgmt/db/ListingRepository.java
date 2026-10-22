package dev.michaelfarrant.simplestore.cataloguemgmt.db;

import dev.michaelfarrant.simplestore.cataloguemgmt.Listing;
import dev.michaelfarrant.simplestore.cataloguemgmt.ListingItem;
import dev.michaelfarrant.simplestore.cataloguemgmt.ListingStatus;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Loads and stores the {@link Listing} aggregate (listing row + its listing_item rows).
 * Concurrent modifications are detected via the version column.
 */
@Repository
public class ListingRepository {

    private final JdbcClient jdbcClient;

    public ListingRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<Listing> get(UUID id) {
        final String selectListing = """
                SELECT id, title, description, status, created, updated, published, withdrawn, version
                FROM listing
                WHERE id = :id
                """;

        final String selectItems = """
                SELECT item_id, quantity
                FROM listing_item
                WHERE listing_id = :id
                ORDER BY item_id
                """;

        // Read the listing before its items so a concurrent change committed between the two reads
        // leaves us with a stale version and is rejected on save.
        Optional<ListingRow> row = jdbcClient.sql(selectListing)
                .param("id", id)
                .query((rs, i) -> new ListingRow(
                        rs.getObject("id", UUID.class),
                        rs.getString("title"),
                        rs.getString("description"),
                        ListingStatus.valueOf(rs.getString("status")),
                        rs.getObject("created", OffsetDateTime.class),
                        rs.getObject("updated", OffsetDateTime.class),
                        rs.getObject("published", OffsetDateTime.class),
                        rs.getObject("withdrawn", OffsetDateTime.class),
                        rs.getInt("version")))
                .optional();

        return row.map(listing -> {
            List<ListingItem> items = jdbcClient.sql(selectItems)
                    .param("id", id)
                    .query((rs, i) -> new ListingItem(rs.getObject("item_id", UUID.class), rs.getInt("quantity")))
                    .list();

            return new Listing(listing.id(), listing.title(), listing.description(), listing.status(), items,
                    listing.created(), listing.updated(), listing.published(), listing.withdrawn(), listing.version());
        });
    }

    public void save(Listing listing) {
        if (listing.isNew()) {
            insert(listing);
        } else {
            update(listing);
        }
        replaceItems(listing);
    }

    private void insert(Listing listing) {
        final String insertListing = """
                INSERT INTO listing (id, title, description, status, created, updated, published, withdrawn, version)
                VALUES (:id, :title, :description, :status, :created, :updated, :published, :withdrawn, 1)
                """;

        jdbcClient.sql(insertListing)
                .param("id", listing.id())
                .param("title", listing.title())
                .param("description", listing.description())
                .param("status", listing.status().name())
                .param("created", listing.created())
                .param("updated", listing.updated())
                .param("published", listing.published().orElse(null))
                .param("withdrawn", listing.withdrawn().orElse(null))
                .update();
    }

    private void update(Listing listing) {
        final String updateListing = """
                UPDATE listing
                SET title = :title,
                    description = :description,
                    status = :status,
                    updated = :updated,
                    published = :published,
                    withdrawn = :withdrawn,
                    version = version + 1
                WHERE id = :id AND version = :version
                """;

        int updated = jdbcClient.sql(updateListing)
                .param("id", listing.id())
                .param("title", listing.title())
                .param("description", listing.description())
                .param("status", listing.status().name())
                .param("updated", listing.updated())
                .param("published", listing.published().orElse(null))
                .param("withdrawn", listing.withdrawn().orElse(null))
                .param("version", listing.version())
                .update();

        if (updated == 0) {
            throw new OptimisticLockingFailureException("Listing %s was modified concurrently".formatted(listing.id()));
        }
    }

    private void replaceItems(Listing listing) {
        jdbcClient.sql("DELETE FROM listing_item WHERE listing_id = :id")
                .param("id", listing.id())
                .update();

        for (ListingItem item : listing.items()) {
            jdbcClient.sql("INSERT INTO listing_item (listing_id, item_id, quantity) VALUES (:listingId, :itemId, :quantity)")
                    .param("listingId", listing.id())
                    .param("itemId", item.itemId())
                    .param("quantity", item.quantity())
                    .update();
        }
    }

    private record ListingRow(
            UUID id,
            String title,
            String description,
            ListingStatus status,
            OffsetDateTime created,
            OffsetDateTime updated,
            OffsetDateTime published,
            OffsetDateTime withdrawn,
            int version) {
    }
}
