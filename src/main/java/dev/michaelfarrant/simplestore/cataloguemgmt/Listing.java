package dev.michaelfarrant.simplestore.cataloguemgmt;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Listing {

    private final UUID id;
    private String title;
    private String description;
    private final OffsetDateTime created;
    private final List<ListingItem> items;

    public Listing(UUID id, String title, String description, OffsetDateTime created, List<ListingItem> items) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.created = created;
        this.items = items;
    }

    public UUID id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public OffsetDateTime created() {
        return created;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean canPublish(){
        // Todo expand this, should have a better return to allow FE decisions.
        return !items.isEmpty();
    }

    public static Listing fromItem(Item item){

        UUID id = item.id();
        OffsetDateTime now = OffsetDateTime.now();
        ListingItem listingItem = ListingItem.fromItem(item, id);
        List<ListingItem> items = List.of(listingItem);

        return new Listing(id, item.title(), "", now, items);
    }


}
