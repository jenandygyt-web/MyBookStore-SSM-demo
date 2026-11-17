package org.csu.mybookstore.demo;

import lombok.Data;

import java.math.BigDecimal;

/** 演示用购物车行。 */
@Data
public class DemoCartLine {

    private DemoBook book;
    private int qty;

    public DemoCartLine(DemoBook book, int qty) {
        this.book = book;
        this.qty = qty;
    }

    public BigDecimal getLineTotal() {
        return book.getPrice().multiply(BigDecimal.valueOf(qty));
    }
}
