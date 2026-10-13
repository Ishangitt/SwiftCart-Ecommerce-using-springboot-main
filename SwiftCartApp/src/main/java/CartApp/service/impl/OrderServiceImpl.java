package CartApp.service.impl;

import CartApp.dto.request.PlaceOrderRequestDTO;
import CartApp.dto.request.UpdateOrderStatusRequestDTO;
import CartApp.dto.response.OrderItemResponseDTO;
import CartApp.dto.response.OrderResponseDTO;
import CartApp.dto.response.PageResponseDTO;
import CartApp.entity.*;
import CartApp.exception.EmptyCartException;
import CartApp.exception.InsufficientStockException;
import CartApp.exception.InvalidOperationException;
import CartApp.exception.ResourceNotFoundException;
import CartApp.repository.*;
import CartApp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public OrderResponseDTO placeOrder(PlaceOrderRequestDTO placeOrderRequestDTO) {
        // Fetch user
        Optional<User> userOptional = userRepository.findById(placeOrderRequestDTO.getUserId());
        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("User", "id", placeOrderRequestDTO.getUserId());
        }
        User user = userOptional.get();

        // Fetch cart with items
        Optional<Cart> cartOptional = cartRepository.findByUserIdWithItems(placeOrderRequestDTO.getUserId());
        if (!cartOptional.isPresent()) {
            throw new ResourceNotFoundException("Cart", "userId", placeOrderRequestDTO.getUserId());
        }
        Cart cart = cartOptional.get();

        // Validate cart is not empty
        if (cart.getCartItems().isEmpty()) {
            throw new EmptyCartException("Cannot place order with an empty cart");
        }

        // Copy cart items to avoid concurrent modification issues
        List<CartItem> cartItemsCopy = new ArrayList<>(cart.getCartItems());

        // Validate stock availability for all items
        for (CartItem cartItem : cartItemsCopy) {
            Product product = cartItem.getProduct();
            if (!product.getIsAvailable()) {
                throw new InsufficientStockException(product.getName() + " is no longer available");
            }
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for " + product.getName() +
                                ". Available: " + product.getStockQuantity() +
                                ", Requested: " + cartItem.getQuantity());
            }
        }

        // Calculate total amount as the sum of all cart item subtotals
        double totalAmount = 0.0;
        for (CartItem cartItem : cartItemsCopy) {
            totalAmount = totalAmount + cartItem.getSubtotal();
        }

        // Create order
        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .notes(placeOrderRequestDTO.getNotes())
                .status(Order.STATUS_CONFIRMED)
                .build();

        Order savedOrder = orderRepository.save(order);

        // Create order items from cart items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItemsCopy) {
            OrderItem orderItem = OrderItem.fromCartItem(cartItem);
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);

            // Decrease product stock
            productRepository.decreaseStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        }
        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        // Clear the cart after order is placed
        cart.getCartItems().clear();
        cart.setTotalItems(0);
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);

        return mapToOrderResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO getOrderById(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findByIdWithItems(orderId);
        if (orderOptional.isPresent()) {
            return mapToOrderResponseDTO(orderOptional.get());
        } else {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }
    }

    @Override
    public OrderResponseDTO getOrderByOrderNumber(String orderNumber) {
        Optional<Order> orderOptional = orderRepository.findByOrderNumberWithItems(orderNumber);
        if (orderOptional.isPresent()) {
            return mapToOrderResponseDTO(orderOptional.get());
        } else {
            throw new ResourceNotFoundException("Order", "orderNumber", orderNumber);
        }
    }

    @Override
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            responseList.add(mapToOrderResponseDTO(order));
        }
        return responseList;
    }

    @Override
    public PageResponseDTO<OrderResponseDTO> getAllOrdersPaginated(int page, int size, String sortBy, String sortDir) {
        Sort sort;
        if (sortDir.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return mapToPageResponse(orderPage);
    }

    @Override
    public List<OrderResponseDTO> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            responseList.add(mapToOrderResponseDTO(order));
        }
        return responseList;
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO) {
        Order order = findOrderById(orderId);

        validateStatusTransition(order.getStatus(), updateOrderStatusRequestDTO.getOrderStatus().toUpperCase());

        order.setStatus(updateOrderStatusRequestDTO.getOrderStatus().toUpperCase());

        if (Order.STATUS_CANCELLED.equals(updateOrderStatusRequestDTO.getOrderStatus().toUpperCase())) {
            restoreStock(order);
        }

        if (updateOrderStatusRequestDTO.getNotes() != null && !updateOrderStatusRequestDTO.getNotes().isEmpty()) {
            String existingNotes = order.getNotes() != null ? order.getNotes() + "\n" : "";
            order.setNotes(existingNotes + "[" + LocalDateTime.now() + "] " + updateOrderStatusRequestDTO.getNotes());
        }

        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponseDTO(updatedOrder);
    }

    @Override
    public OrderResponseDTO cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!Order.STATUS_CONFIRMED.equalsIgnoreCase(order.getStatus())) {
            throw new InvalidOperationException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(Order.STATUS_CANCELLED);

        String notes = order.getNotes() != null ? order.getNotes() + "\n" : "";
        order.setNotes(notes +" Cancelled: " + reason);

        restoreStock(order);

        Order cancelledOrder = orderRepository.save(order);
        return mapToOrderResponseDTO(cancelledOrder);
    }

    @Override
    public PageResponseDTO<OrderResponseDTO> searchOrders(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.searchOrders(keyword, pageable);
        return mapToPageResponse(orderPage);
    }

    // Helper: find order by ID
    private Order findOrderById(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            return orderOptional.get();
        } else {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }
    }

    // Helper: validate status transition
    private void validateStatusTransition(String currentStatus, String newStatus) {
        if (Order.STATUS_CONFIRMED.equals(currentStatus)) {
            if (!Order.STATUS_CANCELLED.equals(newStatus)) {
                throw new InvalidOperationException("Cannot transition from CONFIRMED to " + newStatus);
            }
        } else if (Order.STATUS_CANCELLED.equals(currentStatus)) {
            throw new InvalidOperationException("Cannot change status of " + currentStatus + " order");
        }
    }

    // Helper: restore stock when order is cancelled
    private void restoreStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            if (product != null) {
                product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
            }
        }
    }

    // Helper: map Order to OrderResponseDTO
    private OrderResponseDTO mapToOrderResponseDTO(Order order) {
        List<OrderItemResponseDTO> items = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            items.add(mapToOrderItemResponseDTO(orderItem));
        }

        int totalItems = 0;
        for (OrderItem orderItem : order.getOrderItems()) {
            totalItems = totalItems + orderItem.getQuantity();
        }

        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .userName(order.getUser().getFullName())
                .userEmail(order.getUser().getEmail())
                .items(items)
                .totalItems(totalItems)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .notes(order.getNotes())
                .orderDate(order.getOrderDate())
                .build();
    }

    // Helper: map OrderItem to OrderItemResponseDTO
    private OrderItemResponseDTO mapToOrderItemResponseDTO(OrderItem orderItem) {
        return OrderItemResponseDTO.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProductName())
                .productSku(orderItem.getProductSku())
                .productImage(orderItem.getProduct().getImageUrl())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .subtotal(orderItem.getSubtotal())
                .build();
    }

    // Helper: map Page to PageResponseDTO
    private PageResponseDTO<OrderResponseDTO> mapToPageResponse(Page<Order> orderPage) {
        List<OrderResponseDTO> orders = new ArrayList<>();
        for (Order order : orderPage.getContent()) {
            orders.add(mapToOrderResponseDTO(order));
        }

        return PageResponseDTO.<OrderResponseDTO>builder()
                .content(orders)
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .first(orderPage.isFirst())
                .last(orderPage.isLast())
                .hasNext(orderPage.hasNext())
                .hasPrevious(orderPage.hasPrevious())
                .build();
    }
}
