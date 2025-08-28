package com.order_invoice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.order_invoice.entity.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    private OrderItem item1;
    private OrderItem item2;
    private OrderItem item3;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();

        item1 = new OrderItem(null, "Laptop", "Electronics", 2, 55000.0);
        item2 = new OrderItem(null, "Mobile", "Electronics", 5, 20000.0);
        item3 = new OrderItem(null, "Shirt", "Clothing", 3, 1500.0);

        orderRepository.saveAll(List.of(item1, item2, item3));
    }

    @Test
    @DisplayName("Should find items by category")
    void testFindByCategory() {
        List<OrderItem> electronics = orderRepository.findByCategory("Electronics");

        assertThat(electronics).hasSize(2);
        assertThat(electronics).extracting(OrderItem::getProductName)
                               .containsExactlyInAnyOrder("Laptop", "Mobile");
    }

    @Test
    @DisplayName("Should find items with quantity greater than or equal")
    void testFindByQuantityGreaterThanEqual() {
        List<OrderItem> result = orderRepository.findByQuantityGreaterThanEqual(3);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(OrderItem::getProductName)
                          .containsExactlyInAnyOrder("Mobile", "Shirt");
    }
}
