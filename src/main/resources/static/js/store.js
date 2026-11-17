/* MyBookStore · 演示交互：数量步进、购物车合计、登录/订单标签页 */
(function () {
  "use strict";

  function money(value) {
    return value.toFixed(2);
  }

  /* ---------- 数量步进 ---------- */
  document.querySelectorAll("[data-qty-step]").forEach(function (button) {
    button.addEventListener("click", function () {
      var box = button.closest(".qty");
      var input = box ? box.querySelector("[data-qty-input]") : null;
      if (!input) {
        return;
      }
      var step = parseInt(button.getAttribute("data-qty-step"), 10) || 0;
      var current = parseInt(input.value, 10);
      if (isNaN(current)) {
        current = 1;
      }
      var next = Math.min(99, Math.max(1, current + step));
      input.value = next;
      input.dispatchEvent(new Event("input", { bubbles: true }));
    });
  });

  /* ---------- 购物车合计 ---------- */
  var cartTable = document.getElementById("cartTable");
  if (cartTable) {
    var rows = Array.prototype.slice.call(cartTable.querySelectorAll("tbody tr"));

    var recalc = function () {
      var subtotal = 0;
      var count = 0;

      rows.forEach(function (row) {
        if (row.hidden) {
          return;
        }
        var price = parseFloat(row.getAttribute("data-price")) || 0;
        var input = row.querySelector("[data-qty-input]");
        var qty = parseInt(input ? input.value : "1", 10);
        if (isNaN(qty) || qty < 1) {
          qty = 1;
        }
        if (qty > 99) {
          qty = 99;
        }
        if (input) {
          input.value = qty;
        }
        var lineTotal = price * qty;
        var lineNode = row.querySelector("[data-line-total]");
        if (lineNode) {
          lineNode.textContent = money(lineTotal);
        }
        subtotal += lineTotal;
        count += qty;
      });

      var shipping = subtotal === 0 || subtotal >= 99 ? 0 : 8;
      var total = subtotal + shipping;

      setText("[data-subtotal]", money(subtotal));
      setText("[data-shipping]", money(shipping));
      setText("[data-total]", money(total));
      setText("[data-item-count]", String(count));
    };

    var setText = function (selector, value) {
      document.querySelectorAll(selector).forEach(function (node) {
        node.textContent = value;
      });
    };

    cartTable.addEventListener("input", function (event) {
      if (event.target.matches("[data-qty-input]")) {
        recalc();
      }
    });

    cartTable.addEventListener("click", function (event) {
      var remove = event.target.closest("[data-remove]");
      if (!remove) {
        return;
      }
      event.preventDefault();
      var row = remove.closest("tr");
      if (row) {
        row.hidden = true;
        recalc();
      }
    });

    recalc();
  }

  /* ---------- 登录 / 注册切换 ---------- */
  document.querySelectorAll("[data-tab]").forEach(function (tab) {
    tab.addEventListener("click", function () {
      var group = tab.closest(".tabs");
      if (group) {
        group.querySelectorAll("[data-tab]").forEach(function (item) {
          item.classList.toggle("is-active", item === tab);
        });
      }
      var name = tab.getAttribute("data-tab");
      document.querySelectorAll("[data-panel]").forEach(function (panel) {
        panel.hidden = panel.getAttribute("data-panel") !== name;
      });
    });
  });

  /* ---------- 订单状态筛选 ---------- */
  document.querySelectorAll("[data-order-tab]").forEach(function (tab) {
    tab.addEventListener("click", function () {
      document.querySelectorAll("[data-order-tab]").forEach(function (item) {
        item.classList.toggle("is-active", item === tab);
      });
      var filter = tab.getAttribute("data-order-tab");
      document.querySelectorAll("[data-order-status]").forEach(function (card) {
        card.hidden = filter !== "all" && card.getAttribute("data-order-status") !== filter;
      });
    });
  });
})();
