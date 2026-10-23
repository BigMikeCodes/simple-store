package dev.michaelfarrant.simplestore.cataloguemgmt.features.withdrawlisting;

import dev.michaelfarrant.simplestore.cataloguemgmt.Listing;
import dev.michaelfarrant.simplestore.cataloguemgmt.ListingNotFoundException;
import dev.michaelfarrant.simplestore.cataloguemgmt.db.ListingRepository;
import dev.michaelfarrant.simplestore.cataloguemgmt.events.ListingEventPublisher;
import dev.michaelfarrant.simplestore.cataloguemgmt.events.ListingWithdrawn;
import dev.michaelfarrant.simplestore.cqrs.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Withdraws a draft or active listing. Only listings that were published emit a {@link ListingWithdrawn}
 * event, as drafts were never visible outside catalogue management.
 */
@Service
public class WithdrawListingCommandHandler implements CommandHandler<WithdrawListingCommand, Void> {

    private final ListingRepository listingRepository;
    private final ListingEventPublisher eventPublisher;

    public WithdrawListingCommandHandler(ListingRepository listingRepository, ListingEventPublisher eventPublisher) {
        this.listingRepository = listingRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Void handleCommand(WithdrawListingCommand command) {

        Listing listing = listingRepository.get(command.listingId())
                .orElseThrow(() -> new ListingNotFoundException(command.listingId()));

        listing.withdraw();
        int version = listingRepository.save(listing);

        if (listing.published().isPresent()) {
            eventPublisher.publish(new ListingWithdrawn(
                    UUID.randomUUID(), listing.id(), version, listing.withdrawn().orElseThrow()));
        }
        return null;
    }
}
