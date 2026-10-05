package com.codewithmosh.store.controllers;

import com.codewithmosh.store.DTO.*;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/carts")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/create")
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriBuilder
    ) {
        var cartDto = cartService.createCart();
        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cartId}/item")
    public ResponseEntity<CartItemDto> addCartItem(
            @PathVariable("cartId") UUID cartId,
            @RequestBody AddItemToCartRequest request
    ){
        var cartItemDto=cartService.addItemToCart(cartId,request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCartById(@PathVariable("cartId") UUID cartId) {
        var cartDto=cartService.getCart(cartId);
        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/updateCart/{cartId}/items/{productId}")
    public ResponseEntity<?> updateCartItem(
    @PathVariable("cartId") UUID cartId,
    @PathVariable("productId") Long productId,
    @Valid @RequestBody UpdateCartItemRequest request
    ){
        var carItemDto = cartService.updateCartItem(cartId, productId, request.getQuantity());
        return ResponseEntity.ok(carItemDto);
    }

    @DeleteMapping("/remove/{cartId}/items/{productId}")
    public ResponseEntity<?> deleteCartItem(
            @PathVariable("cartId") UUID cartId,
            @PathVariable("productId")Long productId)
    {
        cartService.removeItemFromCart(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<?> clearCart(
            @PathVariable("cartId") UUID cartId
    ) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<?> handleCartNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorDto("Cart Not Found!")
        );
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorDto("Product Not Found in this Cart!")
        );
    }

}
