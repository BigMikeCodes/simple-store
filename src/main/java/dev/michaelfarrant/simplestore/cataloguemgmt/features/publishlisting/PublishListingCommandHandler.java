package dev.michaelfarrant.simplestore.cataloguemgmt.features.publishlisting;

import dev.michaelfarrant.simplestore.cataloguemgmt.Item;
import dev.michaelfarrant.simplestore.cataloguemgmt.Listing;
import dev.michaelfarrant.simplestore.cataloguemgmt.ListingItem;
import dev.michaelfarrant.simplestore.cataloguemgmt.ListingNotFoundException;
import dev.michaelfarrant.simplestore.cataloguemgmt.db.ItemRepository;
import dev.michaelfarrant.simplestore.cataloguemgmt.db.ListingRepository;
import dev.michaelfarrant.simplestore.cataloguemgmt.events.ListingEventPublisher;
import dev.michaelfarrant.simplestore.cataloguemgmt.events.ListingPublished;
import dev.michaelfarrant.simplestore.cqrs.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Publishes a draft listing. The storefront picks it up asynchronously from the {@link ListingPublished} event.
 */
@Service
public class PublishListingCommandHandler implements CommandHandler<PublishListingCommand, Void> {

    private final ItemRepository itemRepository;
    private final ListingRepository listingRepository;
    private final ListingEventPublisher eventPublisher;

    public PublishListingCommandHandler(
            ItemRepository itemRepository,
            ListingRepository listingRepository,
            ListingEventPublisher eventPublisher) {
        this.itemRepository = itemRepository;
        this.listingRepository = listingRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Void handleCommand(PublishListingCommand command) {

        Listing listing = listingRepository.get(command.listingId())
                .orElseThrow(() -> new ListingNotFoundException(command.listingId()));

        listing.publish();
        listingRepository.save(listing);

        eventPublisher.publish(toEvent(listing));
        return null;
    }

    private ListingPublished toEvent(Listing listing) {
        List<UUID> itemIds = listing.items().stream().map(ListingItem::itemId).toList();
        Map<UUID, Item> itemsById = itemRepository.getAll(itemIds).stream()
                .collect(Collectors.toMap(Item::id, Function.identity()));

        List<ListingPublished.PublishedItem> items = listing.items().stream()
                .map(listingItem -> {
                    // listing_item has a foreign key to item, so the item is always present
                    Item item = itemsById.get(listingItem.itemId());
                    return new ListingPublished.PublishedItem(
                            item.id(), item.code(), item.title(), item.description(), listingItem.quantity());
                })
                .toList();

        return new ListingPublished(
                UUID.randomUUID(),
                listing.id(),
                listing.published().orElseThrow(),
                listing.title(),
                listing.description(),
                items);
    }
}
