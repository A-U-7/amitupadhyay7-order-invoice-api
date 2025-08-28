package com.order_invoice.service;

import com.order_invoice.entity.InvoiceItem;
import com.order_invoice.entity.InvoiceResponse;
import com.order_invoice.entity.OrderItem;
import com.order_invoice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private List<OrderItem> sampleOrderItems;

    @BeforeEach
    void setUp() {
        sampleOrderItems = List.of(
                new OrderItem(1l, "Laptop", "Electronics", 1, 50000),
                new OrderItem(2l, "Mouse", "Electronics", 5, 1000),
                new OrderItem(3l, "T-Shirt", "Clothing", 3, 500)
        );
    }

    @Test
    void generateInvoice_ValidItems_ReturnsInvoiceResponse() {

        when(orderRepository.saveAll(anyList())).thenReturn(sampleOrderItems);


        InvoiceResponse response = invoiceService.generateInvoice(sampleOrderItems);


        assertNotNull(response);
        assertEquals(3, response.getInvoice().size());
        assertTrue(response.getGrandTotal() > 0);


        verify(orderRepository, times(1)).saveAll(anyList());
    }

    @Test
    void generateInvoice_EmptyItems_ThrowsIllegalArgumentException() {

        List<OrderItem> emptyItems = List.of();


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoiceService.generateInvoice(emptyItems));

        assertEquals("Items list cannot be null or empty", exception.getMessage());
        verify(orderRepository, never()).saveAll(anyList());
    }

    @Test
    void generateInvoice_NullItems_ThrowsIllegalArgumentException() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoiceService.generateInvoice(null));

        assertEquals("Items list cannot be null or empty", exception.getMessage());
        verify(orderRepository, never()).saveAll(anyList());
    }

    @Test
    void generateInvoice_InvalidQuantity_ThrowsIllegalArgumentException() {

        List<OrderItem> invalidItems = List.of(
                new OrderItem(1l, "Invalid", "Electronics", 0, 1000) // Quantity = 0
        );


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoiceService.generateInvoice(invalidItems));

        assertTrue(exception.getMessage().contains("Quantity must be greater than 0"));
        verify(orderRepository, never()).saveAll(anyList());
    }

    @Test
    void generateInvoice_InvalidUnitPrice_ThrowsIllegalArgumentException() {

        List<OrderItem> invalidItems = List.of(
                new OrderItem(1l, "Invalid", "Electronics", 1, -100) // UnitPrice = -100
        );


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoiceService.generateInvoice(invalidItems));

        assertTrue(exception.getMessage().contains("Unit price must be greater than 0"));
        verify(orderRepository, never()).saveAll(anyList());
    }

    @Test
    void getAllOrders_ReturnsAllOrders() {

        when(orderRepository.findAll()).thenReturn(sampleOrderItems);


        List<OrderItem> result = invoiceService.getAllOrders();


        assertEquals(3, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrdersByCategory_ValidCategory_ReturnsFilteredOrders() {

        List<OrderItem> electronicsItems = List.of(sampleOrderItems.get(0), sampleOrderItems.get(1));
        when(orderRepository.findByCategory("Electronics")).thenReturn(electronicsItems);


        List<OrderItem> result = invoiceService.getOrdersByCategory("Electronics");


        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(item -> "Electronics".equals(item.getCategory())));
        verify(orderRepository, times(1)).findByCategory("Electronics");
    }

    @Test
    void getBulkOrders_ReturnsOrdersWithQuantityGreaterThanOrEqualFive() {

        List<OrderItem> bulkItems = List.of(sampleOrderItems.get(1)); // Only Mouse has quantity >= 5
        when(orderRepository.findByQuantityGreaterThanEqual(5)).thenReturn(bulkItems);


        List<OrderItem> result = invoiceService.getBulkOrders();


        assertEquals(1, result.size());
        assertEquals("Mouse", result.get(0).getProductName());
        assertEquals(5, result.get(0).getQuantity());
        verify(orderRepository, times(1)).findByQuantityGreaterThanEqual(5);
    }

    @Test
    void getInvoiceByOrderId_ValidId_ReturnsInvoiceResponse() {

        OrderItem orderItem = sampleOrderItems.get(0); // Laptop
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderItem));


        InvoiceResponse response = invoiceService.getInvoiceByOrderId(1L);


        assertNotNull(response);
        assertEquals(1, response.getInvoice().size());

        InvoiceItem invoiceItem = response.getInvoice().get(0);
        assertEquals("Laptop", invoiceItem.getProductName());
        assertEquals(59000, invoiceItem.getLineTotal(), 0.01); // 50000 + 18% tax

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getInvoiceByOrderId_InvalidId_ThrowsIllegalArgumentException() {

        when(orderRepository.findById(999L)).thenReturn(Optional.empty());


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invoiceService.getInvoiceByOrderId(999L));

        assertEquals("Order not found with ID: 999", exception.getMessage());
        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    void getAllInvoices_ReturnsAllInvoices() {

        when(orderRepository.findAll()).thenReturn(sampleOrderItems);


        InvoiceResponse response = invoiceService.getAllInvoices();


        assertNotNull(response);
        assertEquals(3, response.getInvoice().size());
        assertTrue(response.getGrandTotal() > 0);

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void processItem_ElectronicsWithDiscount_CalculatesCorrectLineTotal() {

        OrderItem bulkElectronics = new OrderItem(1l, "Bulk Item", "Electronics", 5, 1000);


        InvoiceItem result = invoiceService.processItem(bulkElectronics);


        assertEquals("Bulk Item", result.getProductName());

        assertEquals(5310, result.getLineTotal(), 0.01);
    }

    @Test
    void processItem_ElectronicsWithoutDiscount_CalculatesCorrectLineTotal() {

        OrderItem electronics = new OrderItem(1l, "Laptop", "Electronics", 1, 50000);


        InvoiceItem result = invoiceService.processItem(electronics);


        assertEquals("Laptop", result.getProductName());

        assertEquals(59000, result.getLineTotal(), 0.01);
    }

    @Test
    void processItem_ClothingWithDiscount_CalculatesCorrectLineTotal() {

        OrderItem bulkClothing = new OrderItem(1l, "Bulk Shirt", "Clothing", 6, 1000);


        InvoiceItem result = invoiceService.processItem(bulkClothing);


        assertEquals("Bulk Shirt", result.getProductName());

        assertEquals(6048, result.getLineTotal(), 0.01);
    }

    @Test
    void processItem_ClothingWithoutDiscount_CalculatesCorrectLineTotal() {

        OrderItem clothing = new OrderItem(1l, "Shirt", "Clothing", 2, 1000);


        InvoiceItem result = invoiceService.processItem(clothing);


        assertEquals("Shirt", result.getProductName());

        assertEquals(2240, result.getLineTotal(), 0.01);
    }

    @Test
    void processItem_GroceryWithDiscount_CalculatesCorrectLineTotal() {

        OrderItem bulkGrocery = new OrderItem(1l, "Bulk Food", "Grocery", 5, 1000);


        InvoiceItem result = invoiceService.processItem(bulkGrocery);


        assertEquals("Bulk Food", result.getProductName());

        assertEquals(4725, result.getLineTotal(), 0.01);
    }

    @Test
    void processItem_UnknownCategory_CalculatesCorrectLineTotal() {

        OrderItem unknown = new OrderItem(1l, "Unknown", "Other", 1, 1000);


        InvoiceItem result = invoiceService.processItem(unknown);


        assertEquals("Unknown", result.getProductName());

        assertEquals(1000, result.getLineTotal(), 0.01);
    }

    @Test
    void processItem_NullCategory_CalculatesCorrectLineTotal() {

        OrderItem nullCategory = new OrderItem(1l, "No Category", null, 1, 1000);


        InvoiceItem result = invoiceService.processItem(nullCategory);


        assertEquals("No Category", result.getProductName());
        assertEquals(1000, result.getLineTotal(), 0.01);
    }

    @Test
    void getTaxRate_Electronics_ReturnsCorrectRate() {

        double rate = invoiceService.getTaxRate("Electronics");


        assertEquals(0.18, rate, 0.001);
    }

    @Test
    void getTaxRate_Clothing_ReturnsCorrectRate() {

        double rate = invoiceService.getTaxRate("Clothing");


        assertEquals(0.12, rate, 0.001);
    }

    @Test
    void getTaxRate_Grocery_ReturnsCorrectRate() {

        double rate = invoiceService.getTaxRate("Grocery");


        assertEquals(0.05, rate, 0.001);
    }

    @Test
    void getTaxRate_UnknownCategory_ReturnsZero() {

        double rate = invoiceService.getTaxRate("Unknown");


        assertEquals(0.0, rate, 0.001);
    }

    @Test
    void getTaxRate_NullCategory_ReturnsZero() {

        double rate = invoiceService.getTaxRate(null);


        assertEquals(0.0, rate, 0.001);
    }

    @Test
    void getTaxRate_CaseInsensitive_ReturnsCorrectRate() {

        double electronicsRate = invoiceService.getTaxRate("ELECTRONICS");
        double clothingRate = invoiceService.getTaxRate("CLOTHING");
        double groceryRate = invoiceService.getTaxRate("GROCERY");


        assertEquals(0.18, electronicsRate, 0.001);
        assertEquals(0.12, clothingRate, 0.001);
        assertEquals(0.05, groceryRate, 0.001);
    }
}