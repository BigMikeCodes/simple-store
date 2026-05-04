package dev.michaelfarrant.simplestore.cataloguemgmt.db;

import dev.michaelfarrant.simplestore.cataloguemgmt.Listing;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ListingRepository {

    public Optional<Listing> get(UUID id) {
        return Optional.empty();
    }

    public void save(Listing listing) {

    }
}
