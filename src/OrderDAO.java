import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for the Order table.
 */
public class OrderDAO {

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM `Order`";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        }
        return 0;
    }

    /**
     * Total revenue for completed orders.
     */
    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total FROM `Order` WHERE status = 'Completed'";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    public List<Order> findAll(String searchTerm, String statusFilter) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT pk_order_id, FK_customer_id, order_date, total_amount, status FROM `Order`");

        List<Object> params = new ArrayList<>();
        boolean hasSearch = searchTerm != null && !searchTerm.trim().isEmpty();
        boolean hasStatus = statusFilter != null && !statusFilter.trim().isEmpty() && !"All".equalsIgnoreCase(statusFilter);

        if (hasSearch || hasStatus) {
            sql.append(" WHERE ");
            boolean first = true;

            if (hasSearch) {
                sql.append("(CAST(pk_order_id AS CHAR) LIKE ? OR CAST(FK_customer_id AS CHAR) LIKE ?)");
                String like = "%" + searchTerm.trim() + "%";
                params.add(like);
                params.add(like);
                first = false;
            }

            if (hasStatus) {
                if (!first) {
                    sql.append(" AND ");
                }
                sql.append("status = ?");
                params.add(statusFilter.trim());
            }
        }

        List<Order> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }
        return result;
    }

    public List<Order> findByCustomer(int customerId) throws SQLException {
        String sql = "SELECT pk_order_id, FK_customer_id, order_date, total_amount, status "
                + "FROM `Order` WHERE FK_customer_id = ?";
        List<Order> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }
        return result;
    }

    public void updateStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE `Order` SET status = ? WHERE pk_order_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }

    /**
     * Returns a comma-separated list of product names for a given order.
     */
    public String getProductSummary(int orderId) throws SQLException {
        String sql = "SELECT GROUP_CONCAT(p.product_name SEPARATOR ', ') AS names "
                + "FROM Order_item oi "
                + "JOIN Product p ON oi.FK_product_id = p.pk_product_id "
                + "WHERE oi.FK_order_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String names = rs.getString("names");
                    return names != null ? names : "";
                }
            }
        }
        return "";
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("pk_order_id"));
        o.setCustomerId(rs.getInt("FK_customer_id"));
        Date d = rs.getDate("order_date");
        if (d != null) {
            o.setOrderDate(new java.util.Date(d.getTime()));
        }
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setStatus(rs.getString("status"));
        return o;
    }
}

