package dev.michaelfarrant.simplestore.cart;

import java.util.UUID;

public class CartKeyUtils {

    public static String toCartKey(UUID userId) {
        return "cart:" + userId.toString();
    }

}
