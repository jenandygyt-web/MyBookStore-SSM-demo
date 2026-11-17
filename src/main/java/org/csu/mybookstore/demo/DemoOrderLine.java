package org.csu.mybookstore.demo;

import lombok.Data;

import java.math.BigDecimal;

/** 演示用订单明细行。 */
@Data
public class DemoOrderLine {

    private DemoBook book;
    private int qty;
    private BigDecimal price;

    public DemoOrderLine(DemoBook book, int qty) {
        this.book = book;
        this.qty = qty;
        this.price = book.getPrice();
    }

    public BigDecimal getLineTotal() {
        return price.multiply(BigDecimal.valueOf(qty));
    }
}
