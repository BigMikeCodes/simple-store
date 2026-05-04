package dev.michaelfarrant.simplestore.cataloguemgmt;

import java.util.UUID;

public class ListingItem {

    public final UUID listingId;
    public final UUID itemId;
    public int quantity;

    public ListingItem(UUID listingId, UUID itemId, int quantity) {
        this.listingId = listingId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public UUID listingId() {
        return listingId;
    }

    public UUID itemId() {
        return itemId;
    }

    public int Quantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        // todo check this is positive
        this.quantity = quantity;
    }

    public static ListingItem fromItem(Item item, UUID listingId) {
        return new ListingItem(listingId, item.id(), 1);
    }

}
