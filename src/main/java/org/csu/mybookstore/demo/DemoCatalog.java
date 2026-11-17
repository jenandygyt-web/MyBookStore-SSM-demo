package org.csu.mybookstore.demo;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 演示数据源：从 classpath:demo/books.json 读取图书与分类，
 * 并在内存中拼出购物车和订单，供页面在没有数据库时也能完整展示。
 * 后续接入数据库时，只需要把本类替换成 Service 查询即可。
 */
@Component
public class DemoCatalog {

    private final List<DemoCategory> categories;
    private final List<DemoBook> books;

    public DemoCatalog(ObjectMapper objectMapper) throws IOException {
        try (InputStream in = new ClassPathResource("demo/books.json").getInputStream()) {
            JsonNode root = objectMapper.readTree(in);
            this.categories = objectMapper.convertValue(root.get("categories"),
                    new TypeReference<List<DemoCategory>>() {});
            this.books = objectMapper.convertValue(root.get("books"),
                    new TypeReference<List<DemoBook>>() {});
        }
    }

    public List<DemoCategory> categories() {
        return categories;
    }

    public List<DemoBook> books() {
        return books;
    }

    public List<DemoBook> byCategory(String code) {
        return books.stream().filter(b -> b.getCategoryCode().equals(code)).toList();
    }

    public long countOf(String code) {
        return books.stream().filter(b -> b.getCategoryCode().equals(code)).count();
    }

    public Optional<DemoBook> find(String sku) {
        return books.stream().filter(b -> b.getSku().equalsIgnoreCase(sku)).findFirst();
    }

    public DemoBook first(String sku) {
        return find(sku).orElseThrow();
    }

    /** 首页「新书上架」：按上新顺序固定挑四本。 */
    public List<DemoBook> newArrivals() {
        return List.of(first("BK-003"), first("BK-006"), first("BK-010"), first("BK-015"));
    }

    /** 首页「编辑推荐」：主推 + 两条短推荐。 */
    public List<DemoBook> editorsPicks() {
        return List.of(first("BK-008"), first("BK-001"), first("BK-015"));
    }

    /** 首页「本周热销」：按销量倒序取前五。 */
    public List<DemoBook> bestsellers() {
        return books.stream()
                .sorted(Comparator.comparing(DemoBook::getSales).reversed())
                .limit(5)
                .toList();
    }

    /** 详情页相关推荐：同分类优先，不足时用其他高评分图书补齐。 */
    public List<DemoBook> related(DemoBook book, int limit) {
        List<DemoBook> result = new ArrayList<>(books.stream()
                .filter(b -> !b.getSku().equals(book.getSku()) && b.getCategoryCode().equals(book.getCategoryCode()))
                .sorted(Comparator.comparing(DemoBook::getRating).reversed())
                .toList());
        if (result.size() < limit) {
            books.stream()
                    .filter(b -> !b.getSku().equals(book.getSku()) && !result.contains(b))
                    .sorted(Comparator.comparing(DemoBook::getRating).reversed())
                    .limit(limit - result.size())
                    .forEach(result::add);
        }
        return result.subList(0, Math.min(limit, result.size()));
    }

    /** 演示购物车：三行，共 4 件。 */
    public List<DemoCartLine> cartLines() {
        return List.of(
                new DemoCartLine(first("BK-001"), 1),
                new DemoCartLine(first("BK-008"), 1),
                new DemoCartLine(first("BK-012"), 2));
    }

    public int cartCount() {
        return cartLines().stream().mapToInt(DemoCartLine::getQty).sum();
    }

    public BigDecimal cartSubtotal() {
        return cartLines().stream()
                .map(DemoCartLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** 满 99 包邮，否则 8 元运费。 */
    public BigDecimal shippingOf(BigDecimal subtotal) {
        return subtotal.compareTo(BigDecimal.valueOf(99)) >= 0 ? BigDecimal.ZERO : BigDecimal.valueOf(8);
    }

    /** 演示订单：等待发货 / 已完成。 */
    public List<DemoOrder> orders() {
        DemoOrder waiting = order(1, "MB20261115001", LocalDate.of(2026, 11, 15), "SHIPPING", "待发货",
                List.of(line("BK-001", 1), line("BK-007", 1)));
        DemoOrder done = order(2, "MB20261108002", LocalDate.of(2026, 11, 8), "DONE", "已完成",
                List.of(line("BK-004", 1)));
        DemoOrder older = order(3, "MB20261028003", LocalDate.of(2026, 10, 28), "DONE", "已完成",
                List.of(line("BK-012", 2), line("BK-013", 1)));
        return List.of(waiting, done, older);
    }

    public Optional<DemoOrder> findOrder(long id) {
        return orders().stream().filter(o -> o.getId() == id).findFirst();
    }

    private DemoOrderLine line(String sku, int qty) {
        return new DemoOrderLine(first(sku), qty);
    }

    private DemoOrder order(long id, String no, LocalDate date, String status, String statusText,
                            List<DemoOrderLine> lines) {
        BigDecimal subtotal = lines.stream()
                .map(DemoOrderLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shipping = shippingOf(subtotal);
        DemoOrder order = new DemoOrder();
        order.setId(id);
        order.setNo(no);
        order.setDate(date);
        order.setStatus(status);
        order.setStatusText(statusText);
        order.setLines(lines);
        order.setSubtotal(subtotal);
        order.setShipping(shipping);
        order.setTotal(subtotal.add(shipping));
        order.setReceiver("陈默");
        order.setPhone("138****6621");
        order.setAddress("上海市杨浦区邯郸路 220 号 3 号楼 502 室");
        order.setPayment("支付宝");
        return order;
    }
}
