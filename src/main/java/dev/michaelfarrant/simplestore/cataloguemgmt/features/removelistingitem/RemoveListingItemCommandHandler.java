package dev.michaelfarrant.simplestore.cataloguemgmt.features.removelistingitem;

import dev.michaelfarrant.simplestore.cataloguemgmt.Listing;
import dev.michaelfarrant.simplestore.cataloguemgmt.ListingNotFoundException;
import dev.michaelfarrant.simplestore.cataloguemgmt.db.ListingRepository;
import dev.michaelfarrant.simplestore.cqrs.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveListingItemCommandHandler implements CommandHandler<RemoveListingItemCommand, Void> {

    private final ListingRepository listingRepository;

    public RemoveListingItemCommandHandler(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Override
    @Transactional
    public Void handleCommand(RemoveListingItemCommand command) {

        Listing listing = listingRepository.get(command.listingId())
                .orElseThrow(() -> new ListingNotFoundException(command.listingId()));

        listing.removeItem(command.itemId());
        listingRepository.save(listing);
        return null;
    }
}
