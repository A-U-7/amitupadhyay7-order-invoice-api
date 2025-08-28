package com.order_invoice.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.text.DecimalFormat;

public class DoubleSerializer extends JsonSerializer<Double> {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        if (value != null) {
            // Format to always show 2 decimal places
            String formatted = df.format(value);
            gen.writeNumber(formatted); // Write as formatted string to force .00
        } else {
            gen.writeNumber("0.00");
        }
    }
}