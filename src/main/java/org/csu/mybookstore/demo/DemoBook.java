package org.csu.mybookstore.demo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/** 演示用图书视图对象，字段与 books.json 对应；后续接入数据库后可替换为实体查询结果。 */
@Data
public class DemoBook {

    private String sku;
    private String title;
    private String author;
    private String categoryCode;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private String publisher;
    private String isbn;
    private Integer pages;
    private String pubDate;
    private Double rating;
    private String tag;
    private Integer sales;
    private String tone;
    private boolean featured;
    private String summary;
    private String intro;
    private String authorBio;
    private List<String> toc;

    /** 是否有折扣价可展示。 */
    public boolean isDiscounted() {
        return originalPrice != null && originalPrice.compareTo(price) > 0;
    }

    /** 折扣百分比，例如 62 表示 6.2 折。 */
    public int getDiscountPercent() {
        if (!isDiscounted()) {
            return 100;
        }
        return price.multiply(BigDecimal.valueOf(100))
                .divide(originalPrice, 0, java.math.RoundingMode.HALF_UP)
                .intValue();
    }
}
