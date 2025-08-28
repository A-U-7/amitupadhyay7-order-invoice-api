
package com.order_invoice.controller;

import com.order_invoice.entity.InvoiceResponse;
import com.order_invoice.entity.OrderItem;
import com.order_invoice.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;


    @PostMapping("/invoice")
    public ResponseEntity<InvoiceResponse> generateInvoice(@Valid @RequestBody OrderRequest request) {
        InvoiceResponse response = invoiceService.generateInvoice(request.getItems());
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrders() {
        List<OrderItem> orders = invoiceService.getAllOrders();
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/category/{category}")
    public ResponseEntity<List<OrderItem>> getOrdersByCategory(@PathVariable String category) {
        List<OrderItem> orders = invoiceService.getOrdersByCategory(category);
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/bulk")
    public ResponseEntity<List<OrderItem>> getBulkOrders() {
        List<OrderItem> bulkOrders = invoiceService.getBulkOrders();
        return ResponseEntity.ok(bulkOrders);
    }


    @GetMapping("/invoice/{orderId}")
    public ResponseEntity<InvoiceResponse> getInvoiceByOrderId(@PathVariable Long orderId) {
        InvoiceResponse invoice = invoiceService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(invoice);
    }


    @GetMapping("/invoice")
    public ResponseEntity<InvoiceResponse> getAllInvoices() {
        InvoiceResponse allInvoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(allInvoices);
    }


    public static class OrderRequest {
        private List<OrderItem> items;

        public List<OrderItem> getItems() { return items; }
        public void setItems(List<OrderItem> items) { this.items = items; }
    }
}