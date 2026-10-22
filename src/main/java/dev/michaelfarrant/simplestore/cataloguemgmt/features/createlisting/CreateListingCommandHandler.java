package dev.michaelfarrant.simplestore.cataloguemgmt.features.createlisting;

import dev.michaelfarrant.simplestore.cataloguemgmt.Item;
import dev.michaelfarrant.simplestore.cataloguemgmt.ItemNotFoundException;
import dev.michaelfarrant.simplestore.cataloguemgmt.Listing;
import dev.michaelfarrant.simplestore.cataloguemgmt.ListingItem;
import dev.michaelfarrant.simplestore.cataloguemgmt.db.ItemRepository;
import dev.michaelfarrant.simplestore.cataloguemgmt.db.ListingRepository;
import dev.michaelfarrant.simplestore.cqrs.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Creates a draft listing, optionally with an initial set of items.
 */
@Service
public class CreateListingCommandHandler implements CommandHandler<CreateListingCommand, UUID> {

    private final ItemRepository itemRepository;
    private final ListingRepository listingRepository;

    public CreateListingCommandHandler(ItemRepository itemRepository, ListingRepository listingRepository) {
        this.itemRepository = itemRepository;
        this.listingRepository = listingRepository;
    }

    @Override
    @Transactional
    public UUID handleCommand(CreateListingCommand command) {

        Set<UUID> itemIds = command.items().stream().map(ItemQuantity::itemId).collect(Collectors.toSet());
        if (itemIds.size() != command.items().size()) {
            throw new IllegalArgumentException("Each item can only appear once in a listing");
        }
        requireItemsExist(itemIds);

        List<ListingItem> items = command.items().stream()
                .map(item -> new ListingItem(item.itemId(), item.quantity()))
                .toList();

        Listing listing = Listing.newListing(command.title(), command.description(), items);
        listingRepository.save(listing);

        return listing.id();
    }

    private void requireItemsExist(Set<UUID> itemIds) {
        Set<UUID> found = itemRepository.getAll(itemIds).stream().map(Item::id).collect(Collectors.toSet());
        Set<UUID> missing = itemIds.stream().filter(id -> !found.contains(id)).collect(Collectors.toSet());
        if (!missing.isEmpty()) {
            throw new ItemNotFoundException(missing);
        }
    }
}
