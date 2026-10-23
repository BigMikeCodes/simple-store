package dev.michaelfarrant.simplestore.storefront.catalogue.listingsync;

import dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingPublished;
import dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingUpdated;
import dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.PublishedItem;
import dev.michaelfarrant.simplestore.storefront.catalogue.ProductDocument;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListingProductMapperTest {

    private final UUID listingId = UUID.randomUUID();

    @Test
    void publishedListingBecomesProductTaggedWithItemCodes() {
        ListingPublished published = ListingPublished.newBuilder()
                .setTitle("title")
                .setDescription("description")
                .addItems(PublishedItem.newBuilder().setCode("CODE-1").setQuantity(1))
                .addItems(PublishedItem.newBuilder().setCode("CODE-2").setQuantity(2))
                .build();

        ProductDocument product = ListingProductMapper.toProduct(listingId, published);

        assertThat(product).isEqualTo(new ProductDocument(listingId, "title", "description", Set.of("CODE-1", "CODE-2")));
    }

    @Test
    void updateReplacesDetailsAndKeepsTags() {
        ProductDocument product = new ProductDocument(listingId, "title", "description", Set.of("CODE-1"));
        ListingUpdated updated = ListingUpdated.newBuilder().setTitle("new title").setDescription("new description").build();

        assertThat(ListingProductMapper.withDetails(product, updated))
                .isEqualTo(new ProductDocument(listingId, "new title", "new description", Set.of("CODE-1")));
    }
}
