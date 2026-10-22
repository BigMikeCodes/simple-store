package dev.michaelfarrant.simplestore.cataloguemgmt;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListingTest {

    private final UUID itemId = UUID.randomUUID();

    @Test
    void newListingIsDraft() {
        Listing listing = Listing.newListing("title", "description", List.of());

        assertThat(listing.status()).isEqualTo(ListingStatus.DRAFT);
        assertThat(listing.isNew()).isTrue();
        assertThat(listing.canPublish()).isFalse();
    }

    @Test
    void cannotPublishWithoutItems() {
        Listing listing = Listing.newListing("title", "description", List.of());

        assertThatThrownBy(listing::publish).isInstanceOf(ListingStateException.class);
        assertThat(listing.status()).isEqualTo(ListingStatus.DRAFT);
    }

    @Test
    void publishActivatesListing() {
        Listing listing = draftWithItem();

        listing.publish();

        assertThat(listing.status()).isEqualTo(ListingStatus.ACTIVE);
        assertThat(listing.published()).isPresent();
        assertThat(listing.canPublish()).isFalse();
    }

    @Test
    void cannotPublishTwice() {
        Listing listing = draftWithItem();
        listing.publish();

        assertThatThrownBy(listing::publish).isInstanceOf(ListingStateException.class);
    }

    @Test
    void draftItemsCanBeChanged() {
        Listing listing = draftWithItem();
        UUID otherItemId = UUID.randomUUID();

        listing.setItemQuantity(itemId, 3);
        listing.setItemQuantity(otherItemId, 1);
        listing.removeItem(itemId);

        assertThat(listing.items()).containsExactly(new ListingItem(otherItemId, 1));
    }

    @Test
    void activeListingItemsAreLocked() {
        Listing listing = draftWithItem();
        listing.publish();

        assertThatThrownBy(() -> listing.setItemQuantity(itemId, 2)).isInstanceOf(ListingStateException.class);
        assertThatThrownBy(() -> listing.setItemQuantity(UUID.randomUUID(), 1)).isInstanceOf(ListingStateException.class);
        assertThatThrownBy(() -> listing.removeItem(itemId)).isInstanceOf(ListingStateException.class);
        assertThat(listing.items()).containsExactly(new ListingItem(itemId, 1));
    }

    @Test
    void activeListingDetailsCanBeCorrected() {
        Listing listing = draftWithItem();
        listing.publish();

        listing.updateDetails("corrected title", "corrected description");

        assertThat(listing.title()).isEqualTo("corrected title");
        assertThat(listing.description()).isEqualTo("corrected description");
        assertThat(listing.status()).isEqualTo(ListingStatus.ACTIVE);
    }

    @Test
    void withdrawnListingIsTerminal() {
        Listing listing = draftWithItem();
        listing.publish();
        listing.withdraw();

        assertThat(listing.status()).isEqualTo(ListingStatus.WITHDRAWN);
        assertThat(listing.withdrawn()).isPresent();
        assertThatThrownBy(listing::withdraw).isInstanceOf(ListingStateException.class);
        assertThatThrownBy(listing::publish).isInstanceOf(ListingStateException.class);
        assertThatThrownBy(() -> listing.updateDetails("t", "d")).isInstanceOf(ListingStateException.class);
    }

    @Test
    void draftCanBeWithdrawn() {
        Listing listing = draftWithItem();

        listing.withdraw();

        assertThat(listing.status()).isEqualTo(ListingStatus.WITHDRAWN);
        assertThat(listing.published()).isEmpty();
    }

    @Test
    void quantityMustBePositive() {
        Listing listing = draftWithItem();

        assertThatThrownBy(() -> listing.setItemQuantity(itemId, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    private Listing draftWithItem() {
        return Listing.newListing("title", "description", List.of(new ListingItem(itemId, 1)));
    }
}
