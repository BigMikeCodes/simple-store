package dev.michaelfarrant.simplestore.cataloguemgmt;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Item {

    private final UUID id;
    private String title;
    private String description;
    private OffsetDateTime created;

    public Item(UUID id, String title, String description, OffsetDateTime created) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.created = created;
    }

    public Item(UUID id) {
        this.id = id;
    }

    public OffsetDateTime created() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public String title() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID id() {
        return id;
    }

    public String description() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Item newItem(String title, String description){
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        return new Item(id, title, description, now);
    }

}
