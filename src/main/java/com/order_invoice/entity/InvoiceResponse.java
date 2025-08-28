
package com.order_invoice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.order_invoice.config.DoubleSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponse {
    private List<InvoiceItem> invoice;

    @JsonSerialize(using = DoubleSerializer.class)
    private double grandTotal;
    

}