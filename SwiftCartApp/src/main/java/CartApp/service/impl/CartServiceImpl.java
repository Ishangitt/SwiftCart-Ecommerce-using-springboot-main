package CartApp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CartApp.controller.UserController;
import CartApp.dto.request.AddToCartRequestDTO;
import CartApp.dto.request.UpdateCartItemRequestDTO;
import CartApp.dto.response.CartItemResponseDTO;
import CartApp.dto.response.CartResponseDTO;
import CartApp.entity.Cart;
import CartApp.entity.CartItem;
import CartApp.entity.Product;
import CartApp.entity.User;
import CartApp.exception.InsufficientStockException;
import CartApp.exception.ResourceNotFoundException;
import CartApp.repository.CartItemRepository;
import CartApp.repository.CartRepository;
import CartApp.repository.ProductRepository;
import CartApp.repository.UserRepository;
import CartApp.service.CartService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserController userController;
    @Override
    public CartResponseDTO getCartByUserId(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToCartResponseDTO(cart);
    }

    @Override
    public CartResponseDTO addItemToCart(Long userId, AddToCartRequestDTO addToCartRequestDTO) {
        Cart cart = getOrCreateCart(userId);
        Product product = findProductById(addToCartRequestDTO.getProductId());

        validateProductAvailability(product, addToCartRequestDTO.getQuantity());

        Optional<CartItem> existingCartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + addToCartRequestDTO.getQuantity();
            validateProductAvailability(product, newQuantity);
            cartItem.setQuantity(newQuantity);
            cartItem.calculateSubtotal();
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(addToCartRequestDTO.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            cartItem.calculateSubtotal();
            cart.addCartItem(cartItem);
            cartItemRepository.save(cartItem);
        }

        cart.recalculateTotals();
        cartRepository.save(cart);

        return mapToCartResponseDTO(cart);
    }

    @Override
    public CartResponseDTO updateCartItem(Long userId, Long cartItemId,
            UpdateCartItemRequestDTO updateCartItemRequestDTO) {
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = findCartItemById(cartItemId);

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("CartItem", "id", cartItemId);
        }

        validateProductAvailability(cartItem.getProduct(), updateCartItemRequestDTO.getQuantity());

        cartItem.setQuantity(updateCartItemRequestDTO.getQuantity());
        cartItem.calculateSubtotal();
        cartItemRepository.save(cartItem);

        cart.recalculateTotals();
        cartRepository.save(cart);

        return mapToCartResponseDTO(cart);
    }

    @Override
    public CartResponseDTO removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = findCartItemById(cartItemId);

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("CartItem", "id", cartItemId);
        }

        cart.removeCartItem(cartItem);
        cartItemRepository.delete(cartItem);

        cart.recalculateTotals();
        cartRepository.save(cart);

        return mapToCartResponseDTO(cart);
    }

    @Override
    public CartResponseDTO clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.clearCart();
        cartRepository.save(cart);

        return mapToCartResponseDTO(cart);
    }

    @Override
    public CartItemResponseDTO getCartItem(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = findCartItemById(cartItemId);

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("CartItem", "id", cartItemId);
        }

        return mapToCartItemResponseDTO(cartItem);
    }

    @Override
    public boolean isProductInCart(Long userId, Long productId) {
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        if (!cartOptional.isPresent()) {
            return false;
        }
        Cart cart = cartOptional.get();
        return cartItemRepository.existsByCartIdAndProductId(cart.getId(), productId);
    }

    @Override
    public CartResponseDTO incrementItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Optional<CartItem> cartItemOptional = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (!cartItemOptional.isPresent()) {
            throw new ResourceNotFoundException("CartItem", "productId", productId);
        }
        CartItem cartItem = cartItemOptional.get();

        int newQuantity = cartItem.getQuantity() + quantity;
        validateProductAvailability(cartItem.getProduct(), newQuantity);

        cartItem.setQuantity(newQuantity);
        cartItem.calculateSubtotal();
        cartItemRepository.save(cartItem);

        cart.recalculateTotals();
        cartRepository.save(cart);

        return mapToCartResponseDTO(cart);
    }

    @Override
    public CartResponseDTO decrementItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        Optional<CartItem> cartItemOptional = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (!cartItemOptional.isPresent()) {
            throw new ResourceNotFoundException("CartItem", "productId", productId);
        }
        CartItem cartItem = cartItemOptional.get();

        int newQuantity = cartItem.getQuantity() - quantity;

        if (newQuantity <= 0) {
            cart.removeCartItem(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(newQuantity);
            cartItem.calculateSubtotal();
            cartItemRepository.save(cartItem);
        }

        cart.recalculateTotals();
        cartRepository.save(cart);

        return mapToCartResponseDTO(cart);
    }

    // Helper: get or create cart for user
    private Cart getOrCreateCart(Long userId) {
        Optional<Cart> cartOptional = cartRepository.findByUserIdWithItems(userId);
        if (cartOptional.isPresent()) {
            return cartOptional.get();
        } else {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Cart newCart = Cart.builder()
                        .user(user)
                        .build();
                return cartRepository.save(newCart);
            } else {
                throw new ResourceNotFoundException("User", "id", userId);
            }
        }
    }

    // Helper: find product by ID
    private Product findProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            return productOptional.get();
        } else {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
    }

    // Helper: find cart item by ID
    private CartItem findCartItemById(Long cartItemId) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
        if (cartItemOptional.isPresent()) {
            return cartItemOptional.get();
        } else {
            throw new ResourceNotFoundException("CartItem", "id", cartItemId);
        }
    }

    // Helper: validate product availability and stock
    private void validateProductAvailability(Product product, int requestedQuantity) {
        if (!product.getIsAvailable()) {
            throw new InsufficientStockException(product.getName() + " is not available");
        }
        if (product.getStockQuantity() < requestedQuantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for " + product.getName() +
                            ". Available: " + product.getStockQuantity() +
                            ", Requested: " + requestedQuantity);
        }
    }

    // Helper: map Cart to CartResponseDTO
    private CartResponseDTO mapToCartResponseDTO(Cart cart) {
        List<CartItemResponseDTO> items = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            items.add(mapToCartItemResponseDTO(cartItem));
        }

        return CartResponseDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .userName(cart.getUser().getFullName())
                .items(items)
                .totalItems(cart.getTotalItems())
                .totalAmount(cart.getTotalAmount())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    // Helper: map CartItem to CartItemResponseDTO
    private CartItemResponseDTO mapToCartItemResponseDTO(CartItem cartItem) {
        Product product = cartItem.getProduct();
        return CartItemResponseDTO.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getImageUrl())
                .productSku(product.getSku())
                .unitPrice(product.getPrice())
                .quantity(cartItem.getQuantity())
                .subtotal(cartItem.getSubtotal())
                .availableStock(product.getStockQuantity())
                .addedAt(cartItem.getAddedAt())
                .build();
    }
}
