package CartApp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CartApp.dto.request.PlaceOrderRequestDTO;
import CartApp.dto.request.UpdateOrderStatusRequestDTO;
import CartApp.dto.response.ApiResponseDTO;
import CartApp.dto.response.OrderResponseDTO;
import CartApp.dto.response.PageResponseDTO;
import CartApp.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    // Place a new order
    @PostMapping
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> placeOrder(
            @Valid @RequestBody PlaceOrderRequestDTO placeOrderRequestDTO) {
        OrderResponseDTO order = orderService.placeOrder(placeOrderRequestDTO);
        return new ResponseEntity<>(
                ApiResponseDTO.success("Order placed successfully", order),
                HttpStatus.CREATED);
    }

     //Get order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> getOrderById(@PathVariable Long orderId) {
        OrderResponseDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponseDTO.success(order));
    }

    //Get order by order number
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> getOrderByOrderNumber(
            @PathVariable String orderNumber) {
        OrderResponseDTO order = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(ApiResponseDTO.success(order));
    }

    // GET /api/orders/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDTO<List<OrderResponseDTO>>> getOrdersByUserId(
            @PathVariable Long userId) {
        List<OrderResponseDTO> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Fetched " + orders.size() + " orders", orders));
    }

    //Get all orders (paginated)
    @GetMapping
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<OrderResponseDTO>>> getAllOrdersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PageResponseDTO<OrderResponseDTO> orders = orderService.getAllOrdersPaginated(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponseDTO.success(orders));
    }

    //Get orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponseDTO<List<OrderResponseDTO>>> getOrdersByStatus(
            @PathVariable String status) {
        List<OrderResponseDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponseDTO.success(orders));
    }

    //Update order status
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO) {
        OrderResponseDTO order = orderService.updateOrderStatus(orderId, updateOrderStatusRequestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("Order status updated successfully", order));
    }

    //Cancel order
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false, defaultValue = "Customer requested cancellation") String reason) {
        OrderResponseDTO order = orderService.cancelOrder(orderId, reason);
        return ResponseEntity.ok(ApiResponseDTO.success("Order cancelled successfully", order));
    }

    // Search orders
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<OrderResponseDTO>>> searchOrders(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponseDTO<OrderResponseDTO> orders = orderService.searchOrders(keyword, page, size);
        return ResponseEntity.ok(ApiResponseDTO.success(orders));
    }
}
