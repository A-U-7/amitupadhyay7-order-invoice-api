
package com.order_invoice.repository;


import com.order_invoice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByCategory(String category);
    List<OrderItem> findByQuantityGreaterThanEqual(int quantity);
}