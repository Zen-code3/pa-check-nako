import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central JDBC connection helper.
 *
 * Adjust the URL, USERNAME, and PASSWORD to match your local database.
 * Schema is expected to contain the tables described in the project
 * (Customer, Product, Cart, Cart_item, Order, Order_item, Payment).
 */
public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/qualimed"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Add mysql-connector-j-*.jar to your classpath.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Tests the database connection. Returns true if successful.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(3);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}

