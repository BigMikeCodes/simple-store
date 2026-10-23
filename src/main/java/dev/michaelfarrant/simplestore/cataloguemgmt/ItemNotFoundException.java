package dev.michaelfarrant.simplestore.cataloguemgmt;

import java.util.Collection;
import java.util.UUID;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(Collection<UUID> itemIds) {
        super("Items not found: %s".formatted(itemIds));
    }

}
