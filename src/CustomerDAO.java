import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for the Customer table.
 */
public class CustomerDAO {

    public void create(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customer (full_name, email, password, contact_number, address, is_admin) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPasswordHash());
            stmt.setString(4, customer.getContactNumber());
            stmt.setString(5, customer.getAddress());
            stmt.setBoolean(6, customer.isAdmin());
            stmt.executeUpdate();
        }
    }

    public Customer findByEmail(String email) throws SQLException {
        String sql = "SELECT pk_customer_id, full_name, email, password, contact_number, address, "
                + "COALESCE(is_admin, 0) AS is_admin FROM Customer WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Customer";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        }
        return 0;
    }

    public List<Customer> findAll(String searchTerm) throws SQLException {
        String baseSql = "SELECT pk_customer_id, full_name, email, password, contact_number, address, COALESCE(is_admin, 0) AS is_admin FROM Customer";
        boolean hasSearch = searchTerm != null && !searchTerm.trim().isEmpty();
        String sql = hasSearch
                ? baseSql + " WHERE full_name LIKE ? OR email LIKE ? OR contact_number LIKE ?"
                : baseSql;

        List<Customer> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (hasSearch) {
                String like = "%" + searchTerm.trim() + "%";
                stmt.setString(1, like);
                stmt.setString(2, like);
                stmt.setString(3, like);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }
        return result;
    }

    public void updateAdminRole(int customerId, boolean isAdmin) throws SQLException {
        String sql = "UPDATE Customer SET is_admin = ? WHERE pk_customer_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isAdmin);
            stmt.setInt(2, customerId);
            stmt.executeUpdate();
        }
    }

    public void deleteById(int customerId) throws SQLException {
        String sql = "DELETE FROM Customer WHERE pk_customer_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.executeUpdate();
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        // Using pk_customer_id column name based on entity description.
        c.setCustomerId(rs.getInt("pk_customer_id"));
        c.setFullName(rs.getString("full_name"));
        c.setEmail(rs.getString("email"));
        c.setPasswordHash(rs.getString("password"));
        c.setContactNumber(rs.getString("contact_number"));
        c.setAddress(rs.getString("address"));
        try {
            c.setAdmin(rs.getBoolean("is_admin"));
        } catch (SQLException e) {
            c.setAdmin(false);
        }
        return c;
    }
}

