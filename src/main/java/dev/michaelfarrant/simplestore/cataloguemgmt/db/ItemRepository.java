package dev.michaelfarrant.simplestore.cataloguemgmt.db;

import dev.michaelfarrant.simplestore.cataloguemgmt.Item;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ItemRepository {

    public void save(Item item) {

    }

    public Optional<Item> get(UUID id) {
        return Optional.empty();
    }

    public List<Item> getAll(Collection<UUID> ids) {
        return List.of();
    }

}
