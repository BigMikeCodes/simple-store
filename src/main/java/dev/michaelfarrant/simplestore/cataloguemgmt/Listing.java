package dev.michaelfarrant.simplestore.cataloguemgmt;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A sellable listing composed of one or more items.
 * <p>
 * Lifecycle: DRAFT -> ACTIVE (published) -> WITHDRAWN. Item composition can only change while
 * DRAFT; once ACTIVE only minor corrections to the title and description are allowed.
 * WITHDRAWN is terminal.
 */
public class Listing {

    private final UUID id;
    private final OffsetDateTime created;
    private final Map<UUID, ListingItem> items;
    // Optimistic locking version; 0 means not yet persisted
    private final int version;

    private String title;
    private String description;
    private ListingStatus status;
    private OffsetDateTime updated;
    private OffsetDateTime published;
    private OffsetDateTime withdrawn;

    public Listing(
            UUID id,
            String title,
            String description,
            ListingStatus status,
            Collection<ListingItem> items,
            OffsetDateTime created,
            OffsetDateTime updated,
            OffsetDateTime published,
            OffsetDateTime withdrawn,
            int version) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.items = new LinkedHashMap<>();
        items.forEach(item -> this.items.put(item.itemId(), item));
        this.created = created;
        this.updated = updated;
        this.published = published;
        this.withdrawn = withdrawn;
        this.version = version;
    }

    public static Listing newListing(String title, String description, Collection<ListingItem> items) {
        OffsetDateTime now = OffsetDateTime.now();
        return new Listing(UUID.randomUUID(), title, description, ListingStatus.DRAFT, items, now, now, null, null, 0);
    }

    public void updateDetails(String title, String description) {
        if (status == ListingStatus.WITHDRAWN) {
            throw new ListingStateException("A withdrawn listing cannot be modified");
        }
        this.title = title;
        this.description = description;
        touch();
    }

    public void setItemQuantity(UUID itemId, int quantity) {
        requireDraft("Items can only be changed while the listing is a draft");
        items.put(itemId, new ListingItem(itemId, quantity));
        touch();
    }

    public void removeItem(UUID itemId) {
        requireDraft("Items can only be changed while the listing is a draft");
        if (items.remove(itemId) != null) {
            touch();
        }
    }

    public boolean canPublish() {
        return status == ListingStatus.DRAFT && !items.isEmpty();
    }

    public void publish() {
        requireDraft("Only a draft listing can be published");
        if (items.isEmpty()) {
            throw new ListingStateException("A listing must contain at least one item to be published");
        }
        status = ListingStatus.ACTIVE;
        published = touch();
    }

    public void withdraw() {
        if (status == ListingStatus.WITHDRAWN) {
            throw new ListingStateException("Listing is already withdrawn");
        }
        status = ListingStatus.WITHDRAWN;
        withdrawn = touch();
    }

    public boolean isNew() {
        return version == 0;
    }

    private void requireDraft(String message) {
        if (status != ListingStatus.DRAFT) {
            throw new ListingStateException(message);
        }
    }

    private OffsetDateTime touch() {
        updated = OffsetDateTime.now();
        return updated;
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

    public ListingStatus status() {
        return status;
    }

    public List<ListingItem> items() {
        return List.copyOf(items.values());
    }

    public OffsetDateTime created() {
        return created;
    }

    public OffsetDateTime updated() {
        return updated;
    }

    public Optional<OffsetDateTime> published() {
        return Optional.ofNullable(published);
    }

    public Optional<OffsetDateTime> withdrawn() {
        return Optional.ofNullable(withdrawn);
    }

    public int version() {
        return version;
    }
}
