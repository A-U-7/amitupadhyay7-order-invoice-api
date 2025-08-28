
package com.order_invoice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Product name is required")
    private String productName;
    
    @NotNull(message = "Category is required")
    private String category;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
    
    @Min(value = 0, message = "Unit price must be positive")
    private double unitPrice;

}