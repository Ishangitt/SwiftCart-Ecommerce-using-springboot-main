package CartApp.service;

import CartApp.dto.response.OrderResponseDTO;
import CartApp.entity.Order;
import CartApp.entity.OrderItem;
import CartApp.entity.Product;
import CartApp.entity.User;
import CartApp.exception.InvalidOperationException;
import CartApp.repository.CartRepository;
import CartApp.repository.OrderItemRepository;
import CartApp.repository.OrderRepository;
import CartApp.repository.ProductRepository;
import CartApp.repository.UserRepository;
import CartApp.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CancelOrderTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository; // Added this

    @Mock
    private CartRepository cartRepository; // Added this

    @Mock
    private UserRepository userRepository; // Added this

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private Product product1;
    private OrderItem item1;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).fullName("Test User").email("test@example.com").build();

        product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .stockQuantity(10)
                .price(100.0)
                .isAvailable(true)
                .build();

        item1 = OrderItem.builder()
                .id(1L)
                .product(product1)
                .quantity(2)
                .productName("Product 1")
                .unitPrice(100.0)
                .subtotal(200.0)
                .build();

        List<OrderItem> items = new ArrayList<>();
        items.add(item1);

        order = Order.builder()
                .id(1L)
                .user(user)
                .orderItems(items)
                .status("CONFIRMED")
                .orderDate(LocalDateTime.now())
                .totalAmount(200.0)
                .orderNumber("ORD-123")
                .build();
    }

    @Test
    void testCancelOrderSuccess() {
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDTO result = orderService.cancelOrder(1L, "Test reason");

        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());
        assertEquals("CANCELLED", order.getStatus());
        
        // Improve verification logic
        assertTrue(order.getNotes().contains("Cancelled: Test reason"));

        // Verify stock updated on the product entity directly
        assertEquals(12, product1.getStockQuantity());
        
        // We no longer call the repository method directly
        // verify(productRepository, times(1)).increaseStock(1L, 2); 
    }

    @Test
    void testCancelOrderInvalidStatus() {
        order.setStatus("SHIPPED"); // Cannot cancel shipped order
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class, () -> orderService.cancelOrder(1L, "Reason"));
        
        verify(productRepository, never()).increaseStock(anyLong(), anyInt());
    }
}
