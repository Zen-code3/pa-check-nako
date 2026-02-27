import javax.swing.JFrame;
import javax.swing.JOptionPane;
import qualimed.model.Customer;

/**
 * Simple in-memory session manager for the currently logged-in user.
 * This is shared across all frames in the application.
 */
public class SessionManager {

    private static Customer currentUser;
    private static boolean isAdmin;

    public static void login(Customer user, boolean admin) {
        currentUser = user;
        isAdmin = admin;
    }

    public static void logout() {
        currentUser = null;
        isAdmin = false;
    }

    public static Customer getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Guard method to ensure the user is logged in before viewing a protected frame.
     * If not logged in, it shows a message and redirects to the login page.
     */
    public static void requireLogin(JFrame currentFrame) {
        if (!isLoggedIn()) {
            JOptionPane.showMessageDialog(
                    currentFrame,
                    "You have to log in first.",
                    "Authentication Required",
                    JOptionPane.WARNING_MESSAGE
            );
            if (currentFrame != null) {
                currentFrame.dispose();
            }
            new LogInpage().setVisible(true);
        }
    }
}

