package com.order_invoice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order_invoice.entity.InvoiceItem;
import com.order_invoice.entity.InvoiceResponse;
import com.order_invoice.entity.OrderItem;
import com.order_invoice.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(invoiceController).build();
    }

    @Test
    void generateInvoice_ShouldReturnInvoiceResponse() throws Exception {

        var request = new InvoiceController.OrderRequest();
        var orderItem = new InvoiceItem("Laptop", "Electronics", 1, 1000.0, 1180.0);
        request.setItems(List.of());

        var expectedResponse = new InvoiceResponse(
            List.of(orderItem),
            1180.0
        );

        when(invoiceService.generateInvoice(anyList())).thenReturn(expectedResponse);


        mockMvc.perform(post("/orders/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grandTotal").value(1180.0));
    }



    @Test
    void getOrdersByCategory_ShouldReturnFilteredOrders() throws Exception {

        var orderItem = new OrderItem(1l,"Laptop", "Electronics", 1, 1000.0);
        when(invoiceService.getOrdersByCategory(anyString())).thenReturn(List.of(orderItem));


        mockMvc.perform(get("/orders/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Electronics"));
    }

    @Test
    void getBulkOrders_ShouldReturnBulkOrders() throws Exception {

        var orderItem = new OrderItem();
        orderItem.setProductName("Laptop");
        orderItem.setCategory("Electronics");
        orderItem.setQuantity(5);
        orderItem.setUnitPrice(1000.0);
        when(invoiceService.getBulkOrders()).thenReturn(List.of(orderItem));


        mockMvc.perform(get("/orders/bulk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(5));
    }

    @Test
    void getInvoiceByOrderId_ShouldReturnInvoice() throws Exception {

        var invoiceItem = new InvoiceItem("Laptop", "Electronics", 1, 1000.0, 1180.0);
        var expectedResponse = new InvoiceResponse(List.of(invoiceItem), 1180.0);
        
        when(invoiceService.getInvoiceByOrderId(anyLong())).thenReturn(expectedResponse);


        mockMvc.perform(get("/orders/invoice/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grandTotal").value(1180.0));
    }


}
