import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import qualimed.model.Product;

/**
 * In-memory cart per customer. Used by Userproducts (Add to Cart) and Usercart.
 */
public final class CartManager {

    private static final ConcurrentHashMap<Integer, List<CartItem>> CART_BY_CUSTOMER = new ConcurrentHashMap<>();

    public static class CartItem {
        private final int productId;
        private final String productName;
        private final double unitPrice;
        private int quantity;

        public CartItem(int productId, String productName, double unitPrice, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }

        public int getProductId() { return productId; }
        public String getProductName() { return productName; }
        public double getUnitPrice() { return unitPrice; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = Math.max(0, quantity); }
        public double getLineTotal() { return unitPrice * quantity; }
    }

    public static List<CartItem> getItems(int customerId) {
        List<CartItem> list = CART_BY_CUSTOMER.get(customerId);
        return list != null ? new ArrayList<>(list) : new ArrayList<>();
    }

    public static void addItem(int customerId, Product product, int quantityToAdd) {
        if (product == null || quantityToAdd <= 0) return;
        CART_BY_CUSTOMER.compute(customerId, (k, list) -> {
            if (list == null) list = new ArrayList<>();
            int pid = product.getProductId();
            for (CartItem item : list) {
                if (item.getProductId() == pid) {
                    item.setQuantity(item.getQuantity() + quantityToAdd);
                    return list;
                }
            }
            list.add(new CartItem(pid, product.getProductName(), product.getPrice(), quantityToAdd));
            return list;
        });
    }

    public static void updateQuantity(int customerId, int productId, int newQuantity) {
        List<CartItem> list = CART_BY_CUSTOMER.get(customerId);
        if (list == null) return;
        if (newQuantity <= 0) {
            removeItem(customerId, productId);
            return;
        }
        for (CartItem item : list) {
            if (item.getProductId() == productId) {
                item.setQuantity(newQuantity);
                break;
            }
        }
    }

    public static void removeItem(int customerId, int productId) {
        List<CartItem> list = CART_BY_CUSTOMER.get(customerId);
        if (list == null) return;
        list.removeIf(item -> item.getProductId() == productId);
    }

    public static void clear(int customerId) {
        CART_BY_CUSTOMER.remove(customerId);
    }

    public static int getItemCount(int customerId) {
        List<CartItem> list = CART_BY_CUSTOMER.get(customerId);
        if (list == null) return 0;
        return list.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public static double getTotal(int customerId) {
        List<CartItem> list = CART_BY_CUSTOMER.get(customerId);
        if (list == null) return 0;
        return list.stream().mapToDouble(CartItem::getLineTotal).sum();
    }
}
