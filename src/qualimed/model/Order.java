package qualimed.model;

import java.util.Date;

/**
 * Order entity matching the Order table.
 *
 * Table: Order
 *  - pk_order_id
 *  - FK_customer_id
 *  - order_date
 *  - total_amount
 *  - status
 */
public class Order {

    private int orderId;
    private int customerId;
    private Date orderDate;
    private double totalAmount;
    private String status;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
