package dev.michaelfarrant.simplestore.cataloguemgmt.features.setlistingitem;

import dev.michaelfarrant.simplestore.cataloguemgmt.ItemNotFoundException;
import dev.michaelfarrant.simplestore.cataloguemgmt.Listing;
import dev.michaelfarrant.simplestore.cataloguemgmt.ListingNotFoundException;
import dev.michaelfarrant.simplestore.cataloguemgmt.db.ItemRepository;
import dev.michaelfarrant.simplestore.cataloguemgmt.db.ListingRepository;
import dev.michaelfarrant.simplestore.cqrs.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Adds an item to a draft listing, or sets its quantity if already present.
 */
@Service
public class SetListingItemCommandHandler implements CommandHandler<SetListingItemCommand, Void> {

    private final ItemRepository itemRepository;
    private final ListingRepository listingRepository;

    public SetListingItemCommandHandler(ItemRepository itemRepository, ListingRepository listingRepository) {
        this.itemRepository = itemRepository;
        this.listingRepository = listingRepository;
    }

    @Override
    @Transactional
    public Void handleCommand(SetListingItemCommand command) {

        Listing listing = listingRepository.get(command.listingId())
                .orElseThrow(() -> new ListingNotFoundException(command.listingId()));

        if (itemRepository.get(command.itemId()).isEmpty()) {
            throw new ItemNotFoundException(List.of(command.itemId()));
        }

        listing.setItemQuantity(command.itemId(), command.quantity());
        listingRepository.save(listing);
        return null;
    }
}
