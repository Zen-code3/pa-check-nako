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

    private static final String URL = "jdbc:mysql://localhost:3306/qualimed";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    static {
        try {
            // Load MySQL JDBC driver (Connector/J 8.x)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // If the driver is missing, we'll see errors when attempting to connect.
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}

