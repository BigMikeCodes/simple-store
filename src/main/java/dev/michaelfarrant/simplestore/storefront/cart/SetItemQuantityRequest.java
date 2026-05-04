package dev.michaelfarrant.simplestore.storefront.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public record SetItemQuantityRequest(
        UUID productId,
        int quantity) {

    @JsonIgnore
    public boolean isLessThanOne() {
        return quantity < 1;
    }

}
