import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for the Product table.
 */
public class ProductDAO {

    public void create(Product product) throws SQLException {
        String sql = "INSERT INTO Product (product_name, description, category, price, stock_quantity, expirydate, image_path) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getDescription());
            stmt.setString(3, product.getCategory());
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, product.getStockQuantity());
            if (product.getExpiryDate() != null) {
                stmt.setDate(6, new Date(product.getExpiryDate().getTime()));
            } else {
                stmt.setDate(6, null);
            }
            stmt.setString(7, product.getImagePath());
            stmt.executeUpdate();
        }
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM Product";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        }
        return 0;
    }

    public Product findById(int productId) throws SQLException {
        String sql = "SELECT pk_product_id, product_name, description, category, price, stock_quantity, expirydate, image_path FROM Product WHERE pk_product_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public void update(Product product) throws SQLException {
        String sql = "UPDATE Product SET product_name=?, description=?, category=?, price=?, stock_quantity=?, expirydate=?, image_path=? WHERE pk_product_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getDescription());
            stmt.setString(3, product.getCategory());
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, product.getStockQuantity());
            if (product.getExpiryDate() != null) {
                stmt.setDate(6, new Date(product.getExpiryDate().getTime()));
            } else {
                stmt.setDate(6, null);
            }
            stmt.setString(7, product.getImagePath());
            stmt.setInt(8, product.getProductId());
            stmt.executeUpdate();
        }
    }

    public void deleteById(int productId) throws SQLException {
        String sql = "DELETE FROM Product WHERE pk_product_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
    }

    public List<Product> findAll(String searchTerm) throws SQLException {
        String baseSql = "SELECT pk_product_id, product_name, description, category, price, stock_quantity, expirydate, image_path FROM Product";
        boolean hasSearch = searchTerm != null && !searchTerm.trim().isEmpty();
        String sql = hasSearch
                ? baseSql + " WHERE product_name LIKE ? OR category LIKE ?"
                : baseSql;

        List<Product> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (hasSearch) {
                String like = "%" + searchTerm.trim() + "%";
                stmt.setString(1, like);
                stmt.setString(2, like);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }
        return result;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("pk_product_id"));
        p.setProductName(rs.getString("product_name"));
        p.setDescription(rs.getString("description"));
        p.setCategory(rs.getString("category"));
        p.setPrice(rs.getDouble("price"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        Date expiry = rs.getDate("expirydate");
        if (expiry != null) {
            p.setExpiryDate(new java.util.Date(expiry.getTime()));
        }
        p.setImagePath(rs.getString("image_path"));
        return p;
    }
}

