
package com.order_invoice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.order_invoice.config.DoubleSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItem {
    private String productName;
    private String category;
    private int quantity;
    private double unitPrice;
    @JsonSerialize(using = DoubleSerializer.class)
    private double lineTotal;
    

}