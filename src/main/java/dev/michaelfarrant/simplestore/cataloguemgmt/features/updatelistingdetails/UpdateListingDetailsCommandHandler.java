package dev.michaelfarrant.simplestore.cataloguemgmt.features.updatelistingdetails;

import dev.michaelfarrant.simplestore.cataloguemgmt.Listing;
import dev.michaelfarrant.simplestore.cataloguemgmt.ListingNotFoundException;
import dev.michaelfarrant.simplestore.cataloguemgmt.ListingStatus;
import dev.michaelfarrant.simplestore.cataloguemgmt.db.ListingRepository;
import dev.michaelfarrant.simplestore.cataloguemgmt.events.ListingEventPublisher;
import dev.michaelfarrant.simplestore.cataloguemgmt.events.ListingUpdated;
import dev.michaelfarrant.simplestore.cqrs.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Updates a listing's title and description. Allowed for drafts and, as a minor correction,
 * for active listings, in which case a {@link ListingUpdated} event is published.
 */
@Service
public class UpdateListingDetailsCommandHandler implements CommandHandler<UpdateListingDetailsCommand, Void> {

    private final ListingRepository listingRepository;
    private final ListingEventPublisher eventPublisher;

    public UpdateListingDetailsCommandHandler(ListingRepository listingRepository, ListingEventPublisher eventPublisher) {
        this.listingRepository = listingRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Void handleCommand(UpdateListingDetailsCommand command) {

        Listing listing = listingRepository.get(command.listingId())
                .orElseThrow(() -> new ListingNotFoundException(command.listingId()));

        listing.updateDetails(command.title(), command.description());
        listingRepository.save(listing);

        if (listing.status() == ListingStatus.ACTIVE) {
            eventPublisher.publish(new ListingUpdated(
                    UUID.randomUUID(), listing.id(), listing.updated(), listing.title(), listing.description()));
        }
        return null;
    }
}
