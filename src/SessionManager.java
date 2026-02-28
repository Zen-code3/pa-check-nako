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
     * Shows "Required login first" message. Use for F6 shortcut when not logged in.
     */
    public static void showLoginRequiredMessage(JFrame parent) {
        JOptionPane.showMessageDialog(
                parent,
                "Required login first.",
                "Authentication Required",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Guard method to ensure the user is logged in before viewing a protected frame.
     * If not logged in, it shows a message and redirects to the login page.
     */
    public static void requireLogin(JFrame currentFrame) {
        if (!isLoggedIn()) {
            showLoginRequiredMessage(currentFrame);
            if (currentFrame != null) {
                currentFrame.dispose();
            }
            new LogInpage().setVisible(true);
        }
    }
}

