package dev.michaelfarrant.simplestore.cart;

import dev.michaelfarrant.simplestore.cart.getcart.GetCartQuery;
import dev.michaelfarrant.simplestore.cart.getcart.GetCartQueryHandler;
import dev.michaelfarrant.simplestore.cart.setitemquantity.SetItemQuantityCommand;
import dev.michaelfarrant.simplestore.cart.setitemquantity.SetItemQuantityCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/cart")
public class CartRestController {

    private final GetCartQueryHandler getCartQueryHandler;
    private final SetItemQuantityCommandHandler setItemQuantityCommandHandler;

    public CartRestController(
            GetCartQueryHandler getCartQueryHandler,
            SetItemQuantityCommandHandler setItemQuantityCommandHandler) {
        this.getCartQueryHandler = getCartQueryHandler;
        this.setItemQuantityCommandHandler = setItemQuantityCommandHandler;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(){
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        GetCartQuery query = new GetCartQuery(userId);
        CartResponse cart = getCartQueryHandler.handle(query);

        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public void setCartItemQuantity(@RequestBody SetItemQuantityRequest command){
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        SetItemQuantityCommand cmd = new SetItemQuantityCommand(userId, command.productId(), command.quantity());
        setItemQuantityCommandHandler.handle(cmd);
    }
}
