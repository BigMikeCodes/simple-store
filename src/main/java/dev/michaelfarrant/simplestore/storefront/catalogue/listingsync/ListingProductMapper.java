package dev.michaelfarrant.simplestore.storefront.catalogue.listingsync;

import dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingPublished;
import dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.ListingUpdated;
import dev.michaelfarrant.simplestore.contracts.cataloguemgmt.listing.v1.PublishedItem;
import dev.michaelfarrant.simplestore.storefront.catalogue.ProductDocument;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A published listing is what the storefront sells as a product; its item codes become the product's tags.
 */
final class ListingProductMapper {

    private ListingProductMapper() {
    }

    static ProductDocument toProduct(UUID listingId, ListingPublished published) {
        return new ProductDocument(
                listingId,
                published.getTitle(),
                published.getDescription(),
                published.getItemsList().stream().map(PublishedItem::getCode).collect(Collectors.toSet()));
    }

    static ProductDocument withDetails(ProductDocument product, ListingUpdated updated) {
        return new ProductDocument(product.id(), updated.getTitle(), updated.getDescription(), product.tags());
    }
}
