package qualimed.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import qualimed.database.Database;
import qualimed.dao.OrderDAO;
import qualimed.dao.ProductDAO;
import qualimed.model.Order;
import qualimed.model.OrderItem;
import qualimed.model.Product;

/**
 * Transactional checkout: order creation, order items, stock decrement, all in one ACID transaction.
 * On failure, everything is rolled back.
 */
public class TransactionService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAO productDAO = new ProductDAO();

    /**
     * Result of a completed transaction.
     */
    public static class TransactionResult {
        private final Order order;
        private final List<OrderItem> items;

        public TransactionResult(Order order, List<OrderItem> items) {
            this.order = order;
            this.items = items;
        }
        public Order getOrder() { return order; }
        public List<OrderItem> getItems() { return items; }
    }

    /**
     * Execute checkout in a single transaction: create order, add items, decrement stock.
     * Uses cart item format (productId, productName, unitPrice, quantity).
     * @param customerId customer placing the order
     * @param cartItems list of {productId, productName, unitPrice, quantity}
     * @param totalAmount order total
     * @return TransactionResult with order and items, or null on failure
     */
    public TransactionResult checkout(int customerId, List<CartLine> cartItems, double totalAmount) throws SQLException {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            // 1. Create order
            int orderId = orderDAO.createOrder(conn, customerId, totalAmount, "Pending");

            // 2. Add order items and decrement stock
            for (CartLine line : cartItems) {
                Product p = productDAO.findById(conn, line.productId);
                if (p == null) throw new SQLException("Product not found: " + line.productId);
                int stock = p.getStockQuantity();
                if (stock < line.quantity) {
                    throw new SQLException("Insufficient stock for " + line.productName + ": requested " + line.quantity + ", available " + stock);
                }
                orderDAO.addOrderItem(conn, orderId, line.productId, line.quantity, line.unitPrice);
                productDAO.decrementStock(conn, line.productId, line.quantity);
            }

            conn.commit();

            Order order = new Order();
            order.setOrderId(orderId);
            order.setCustomerId(customerId);
            order.setTotalAmount(totalAmount);
            order.setStatus("Pending");

            List<OrderItem> items = orderDAO.getOrderItems(conn, orderId);
            conn.setAutoCommit(true);
            return new TransactionResult(order, items);

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            throw e instanceof SQLException ? (SQLException) e : new SQLException(e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    /** Simple cart line for checkout. */
    public static class CartLine {
        public final int productId;
        public final String productName;
        public final double unitPrice;
        public final int quantity;

        public CartLine(int productId, String productName, double unitPrice, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }
    }
}
