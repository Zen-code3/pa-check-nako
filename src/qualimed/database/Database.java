package qualimed.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Central JDBC connection helper. Uses SQLite (qualimed.db in project root).
 * Call {@link #initSchema()} on startup to create tables if they do not exist.
 */
public class Database {

    private static final String URL = "jdbc:sqlite:qualimed.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found. Add sqlite-jdbc-*.jar to your project libraries (e.g. lib folder).");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
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

    /**
     * Creates application tables if they do not exist (Customer, addproduct, Order).
     * Safe to call on every startup.
     */
    public static void initSchema() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Customer ("
                + " pk_customer_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " full_name VARCHAR(255) NOT NULL,"
                + " email VARCHAR(255) NOT NULL UNIQUE,"
                + " password VARCHAR(255) NOT NULL,"
                + " contact_number VARCHAR(50),"
                + " address VARCHAR(500),"
                + " is_admin INTEGER DEFAULT 0"
                + ")"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS addproduct ("
                + " pk_product_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " product_name VARCHAR(255) NOT NULL,"
                + " description TEXT,"
                + " category VARCHAR(100),"
                + " price REAL NOT NULL,"
                + " stock_quantity INTEGER NOT NULL DEFAULT 0,"
                + " expirydate TEXT,"
                + " image_path VARCHAR(500)"
                + ")"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS \"Order\" ("
                + " pk_order_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " FK_customer_id INTEGER NOT NULL,"
                + " order_date TEXT,"
                + " total_amount REAL DEFAULT 0,"
                + " status VARCHAR(50),"
                + " FOREIGN KEY (FK_customer_id) REFERENCES Customer(pk_customer_id)"
                + ")"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Order_item ("
                + " pk_order_item_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " FK_order_id INTEGER NOT NULL,"
                + " FK_product_id INTEGER NOT NULL,"
                + " quantity INTEGER NOT NULL,"
                + " unit_price REAL NOT NULL,"
                + " FOREIGN KEY (FK_order_id) REFERENCES \"Order\"(pk_order_id),"
                + " FOREIGN KEY (FK_product_id) REFERENCES addproduct(pk_product_id)"
                + ")"
            );
        }
    }
}
