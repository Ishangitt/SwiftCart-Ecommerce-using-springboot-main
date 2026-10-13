package CartApp.controller;

import CartApp.dto.request.AddToCartRequestDTO;
import CartApp.dto.request.UpdateCartItemRequestDTO;
import CartApp.dto.response.ApiResponseDTO;
import CartApp.dto.response.CartItemResponseDTO;
import CartApp.dto.response.CartResponseDTO;
import CartApp.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    /**
     * Get cart by user ID
     * GET /api/cart/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDTO<CartResponseDTO>> getCart(@PathVariable Long userId) {
        CartResponseDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(ApiResponseDTO.success(cart));
    }

    /**
     * Add item to cart
     * POST /api/cart/{userId}/items
     */
    @PostMapping("/{userId}/items")
    public ResponseEntity<ApiResponseDTO<CartResponseDTO>> addItemToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequestDTO addToCartRequestDTO) {
        CartResponseDTO cart = cartService.addItemToCart(userId, addToCartRequestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("Item added to cart successfully", cart));
    }

    /**
     * Update cart item quantity
     * PUT /api/cart/{userId}/items/{cartItemId}
     */
    @PutMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<ApiResponseDTO<CartResponseDTO>> updateCartItem(
            @PathVariable Long userId,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequestDTO updateCartItemRequestDTO) {
        CartResponseDTO cart = cartService.updateCartItem(userId, cartItemId, updateCartItemRequestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("Cart item updated successfully", cart));
    }

    /**
     * Remove item from cart by cart item ID
     * DELETE /api/cart/{userId}/items/{cartItemId}
     */
    @DeleteMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<ApiResponseDTO<CartResponseDTO>> removeItemFromCart(
            @PathVariable Long userId,
            @PathVariable Long cartItemId) {
        CartResponseDTO cart = cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.ok(ApiResponseDTO.success("Item removed from cart successfully", cart));
    }

    /**
     * Clear entire cart
     * DELETE /api/cart/{userId}/clear
     */
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<ApiResponseDTO<CartResponseDTO>> clearCart(@PathVariable Long userId) {
        CartResponseDTO cart = cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Cart cleared successfully", cart));
    }

    /**
     * Get specific cart item
     * GET /api/cart/{userId}/items/{cartItemId}
     */
    @GetMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<ApiResponseDTO<CartItemResponseDTO>> getCartItem(
            @PathVariable Long userId,
            @PathVariable Long cartItemId) {
        CartItemResponseDTO cartItem = cartService.getCartItem(userId, cartItemId);
        return ResponseEntity.ok(ApiResponseDTO.success(cartItem));
    }

    /**
     * Check if product is in cart
     * GET /api/cart/{userId}/check-product/{productId}
     */
    @GetMapping("/{userId}/check-product/{productId}")
    public ResponseEntity<ApiResponseDTO<Boolean>> isProductInCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        boolean inCart = cartService.isProductInCart(userId, productId);
        return ResponseEntity.ok(ApiResponseDTO.success(
                inCart ? "Product is in cart" : "Product is not in cart", inCart));
    }

    /**
     * Increment item quantity
     * PATCH /api/cart/{userId}/products/{productId}/increment?quantity=1
     */
    @PatchMapping("/{userId}/products/{productId}/increment")
    public ResponseEntity<ApiResponseDTO<CartResponseDTO>> incrementItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        CartResponseDTO cart = cartService.incrementItemQuantity(userId, productId, quantity);
        return ResponseEntity.ok(ApiResponseDTO.success("Quantity incremented successfully", cart));
    }

    /**
     * Decrement item quantity
     * PATCH /api/cart/{userId}/products/{productId}/decrement?quantity=1
     */
    @PatchMapping("/{userId}/products/{productId}/decrement")
    public ResponseEntity<ApiResponseDTO<CartResponseDTO>> decrementItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        CartResponseDTO cart = cartService.decrementItemQuantity(userId, productId, quantity);
        return ResponseEntity.ok(ApiResponseDTO.success("Quantity decremented successfully", cart));
    }
}
