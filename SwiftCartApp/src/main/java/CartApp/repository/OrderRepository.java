package CartApp.repository;

import CartApp.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

       // Find orders by user ID
       List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

       // Find orders by user ID with pagination
       Page<Order> findByUserId(Long userId, Pageable pageable);

       // Find orders by status
       List<Order> findByStatus(String status);

       // Find order with items
       @Query("SELECT o FROM Order o " +
                     "LEFT JOIN FETCH o.orderItems oi " +
                     "LEFT JOIN FETCH oi.product " +
                     "WHERE o.id = :orderId")
       Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);

       // Find order by order number with items
       @Query("SELECT o FROM Order o " +
                     "LEFT JOIN FETCH o.orderItems oi " +
                     "LEFT JOIN FETCH oi.product " +
                     "WHERE o.orderNumber = :orderNumber")
       Optional<Order> findByOrderNumberWithItems(@Param("orderNumber") String orderNumber);

       // Search orders
       @Query("SELECT o FROM Order o WHERE " +
                     "o.orderNumber LIKE %:keyword% " +
                     "OR o.user.fullName LIKE %:keyword% " +
                     "OR o.user.email LIKE %:keyword%")
       Page<Order> searchOrders(@Param("keyword") String keyword, Pageable pageable);
}
