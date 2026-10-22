package dev.michaelfarrant.simplestore.cataloguemgmt.db;

import dev.michaelfarrant.simplestore.cataloguemgmt.Item;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ItemRepository {

    private static final RowMapper<Item> ITEM_MAPPER = (rs, i) -> new Item(
            rs.getObject("id", UUID.class),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("code"),
            rs.getObject("created", OffsetDateTime.class));

    private final JdbcClient jdbcClient;

    public ItemRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void save(Item item) {
        final String upsertItem = """
                INSERT INTO item (id, code, title, description, created)
                VALUES (:id, :code, :title, :description, :created)
                ON CONFLICT (id) DO UPDATE
                SET title = EXCLUDED.title, description = EXCLUDED.description
                """;

        jdbcClient.sql(upsertItem)
                .param("id", item.id())
                .param("code", item.code())
                .param("title", item.title())
                .param("description", item.description())
                .param("created", item.created())
                .update();
    }

    public Optional<Item> get(UUID id) {
        return jdbcClient.sql("SELECT id, code, title, description, created FROM item WHERE id = :id")
                .param("id", id)
                .query(ITEM_MAPPER)
                .optional();
    }

    public List<Item> getAll(Collection<UUID> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return jdbcClient.sql("SELECT id, code, title, description, created FROM item WHERE id IN (:ids)")
                .param("ids", ids)
                .query(ITEM_MAPPER)
                .list();
    }

}
