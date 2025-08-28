
package com.order_invoice.service;


import com.order_invoice.entity.InvoiceItem;
import com.order_invoice.entity.InvoiceResponse;
import com.order_invoice.entity.OrderItem;
import com.order_invoice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InvoiceService {


    private OrderRepository orderRepository;


    public InvoiceResponse generateInvoice(List<OrderItem> items) {

        validateItems(items);


        List<OrderItem> savedItems = orderRepository.saveAll(items);


        List<InvoiceItem> invoiceItems = savedItems.stream()
                .map(this::processItem)
                .collect(Collectors.toList());


        double grandTotal = invoiceItems.stream()
                .mapToDouble(InvoiceItem::getLineTotal)
                .sum();

        return new InvoiceResponse(invoiceItems, grandTotal);
    }


    public List<OrderItem> getAllOrders() {
        return orderRepository.findAll();
    }


    public List<OrderItem> getOrdersByCategory(String category) {
        return orderRepository.findByCategory(category);
    }


    public List<OrderItem> getBulkOrders() {
        return orderRepository.findByQuantityGreaterThanEqual(5);
    }


    public InvoiceResponse getInvoiceByOrderId(Long orderId) {
        OrderItem orderItem = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        InvoiceItem invoiceItem = processItem(orderItem);

        return new InvoiceResponse(List.of(invoiceItem), invoiceItem.getLineTotal());
    }


    public InvoiceResponse getAllInvoices() {
        List<OrderItem> allOrders = orderRepository.findAll();

        List<InvoiceItem> invoiceItems = allOrders.stream()
                .map(this::processItem)
                .collect(Collectors.toList());

        double grandTotal = invoiceItems.stream()
                .mapToDouble(InvoiceItem::getLineTotal)
                .sum();

        return new InvoiceResponse(invoiceItems, grandTotal);
    }


    protected InvoiceItem processItem(OrderItem item) {

        double lineTotal = item.getQuantity() * item.getUnitPrice();


        if (item.getQuantity() >= 5) {
            lineTotal *= 0.9;
        }


        double taxRate = getTaxRate(item.getCategory());
        lineTotal *= (1 + taxRate);

        return new InvoiceItem(
                item.getProductName(),
                item.getCategory(),
                item.getQuantity(),
                item.getUnitPrice(),
                lineTotal
        );
    }

    public double getTaxRate(String category) {
        if (category == null) return 0.0;

        switch (category.toLowerCase()) {
            case "electronics":
                return 0.18;
            case "clothing":
                return 0.12;
            case "grocery":
                return 0.05;
            default:
                return 0.0;
        }
    }

    private void validateItems(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Items list cannot be null or empty");
        }

        items.forEach(item -> {
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0 for all items");
            }
            if (item.getUnitPrice() <= 0) {
                throw new IllegalArgumentException("Unit price must be greater than 0 for all items");
            }
        });
    }
}