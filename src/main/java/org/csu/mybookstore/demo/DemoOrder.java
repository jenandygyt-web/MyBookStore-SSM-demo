package org.csu.mybookstore.demo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** 演示用订单。 */
@Data
public class DemoOrder {

    private long id;
    private String no;
    private LocalDate date;
    private String status;
    private String statusText;
    private List<DemoOrderLine> lines;
    private BigDecimal subtotal;
    private BigDecimal shipping;
    private BigDecimal total;
    private String receiver;
    private String phone;
    private String address;
    private String payment;

    public int getItemCount() {
        return lines.stream().mapToInt(DemoOrderLine::getQty).sum();
    }
}
