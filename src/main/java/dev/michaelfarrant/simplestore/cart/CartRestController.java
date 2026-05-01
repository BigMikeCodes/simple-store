package dev.michaelfarrant.simplestore.cart;

import dev.michaelfarrant.simplestore.cart.getcart.GetCartQuery;
import dev.michaelfarrant.simplestore.cart.getcart.GetCartQueryHandler;
import dev.michaelfarrant.simplestore.cart.removeitem.RemoveItemCommand;
import dev.michaelfarrant.simplestore.cart.removeitem.RemoveItemCommandHandler;
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
    private final RemoveItemCommandHandler removeItemCommandHandler;

    public CartRestController(
            GetCartQueryHandler getCartQueryHandler,
            SetItemQuantityCommandHandler setItemQuantityCommandHandler,
            RemoveItemCommandHandler removeItemCommandHandler) {
        this.getCartQueryHandler = getCartQueryHandler;
        this.setItemQuantityCommandHandler = setItemQuantityCommandHandler;
        this.removeItemCommandHandler = removeItemCommandHandler;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(){
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        GetCartQuery query = new GetCartQuery(userId);
        CartResponse cart = getCartQueryHandler.handle(query);

        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public void setCartItemQuantity(@RequestBody SetItemQuantityRequest req){
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        if(req.isLessThanOne()) {
            RemoveItemCommand cmd = new RemoveItemCommand(req.productId(), userId);
            removeItemCommandHandler.handleCommand(cmd);
        }
        else {
            SetItemQuantityCommand cmd = new SetItemQuantityCommand(userId, req.productId(), req.quantity());
            setItemQuantityCommandHandler.handle(cmd);
        }
    }

    @DeleteMapping("{productId}")
    public void removeItem(@PathVariable UUID productId){
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        RemoveItemCommand cmd = new RemoveItemCommand(productId, userId);
        removeItemCommandHandler.handleCommand(cmd);
    }
}
