package dev.michaelfarrant.simplestore.cataloguemgmt.features.listingfromitem;

import dev.michaelfarrant.simplestore.cataloguemgmt.Item;
import dev.michaelfarrant.simplestore.cataloguemgmt.Listing;
import dev.michaelfarrant.simplestore.cataloguemgmt.db.ItemRepository;
import dev.michaelfarrant.simplestore.cataloguemgmt.db.ListingRepository;
import dev.michaelfarrant.simplestore.cqrs.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class CreateListingFromItemCommandHandler implements CommandHandler<CreateListingFromItemCommand, UUID> {
    private final ItemRepository itemRepository;
    private final ListingRepository listingRepository;

    public CreateListingFromItemCommandHandler(ItemRepository itemRepository, ListingRepository listingRepository) {
        this.itemRepository = itemRepository;
        this.listingRepository = listingRepository;
    }

    @Override
    @Transactional
    public UUID handleCommand(CreateListingFromItemCommand command) {
        // 1. Search the repository for the item
        Optional<Item> itemOptional = itemRepository.get(command.itemId());

        if(itemOptional.isEmpty()){
            // return a result indicating failure
            return null;
        }

        Item item = itemOptional.get();
        Listing listing = Listing.fromItem(item);
        listingRepository.save(listing);

        return listing.id();
    }
}
