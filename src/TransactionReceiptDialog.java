import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import qualimed.model.Order;
import qualimed.model.OrderItem;

/**
 * Transaction receipt dialog shown after successful checkout.
 * Displays order details and line items.
 */
public class TransactionReceiptDialog extends JDialog {

    public TransactionReceiptDialog(JFrame owner, Order order, List<OrderItem> items) {
        super(owner, "Transaction Receipt", true);
        setLayout(new BorderLayout(15, 15));
        setMinimumSize(new Dimension(480, 400));
        getContentPane().setBackground(new Color(248, 249, 250));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(34, 156, 129));
        JLabel title = new JLabel("Order Confirmed");
        title.setFont(new Font("Tahoma", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(10, 10));
        body.setBackground(new Color(248, 249, 250));
        body.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel orderIdLbl = new JLabel("Order #" + order.getOrderId());
        orderIdLbl.setFont(new Font("Tahoma", Font.BOLD, 14));
        body.add(orderIdLbl, BorderLayout.NORTH);

        String[] cols = {"Product", "Qty", "Unit Price", "Total"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        for (OrderItem item : items) {
            model.addRow(new Object[]{
                item.getProductName(),
                item.getQuantity(),
                String.format("₱%.2f", item.getUnitPrice()),
                String.format("₱%.2f", item.getLineTotal())
            });
        }
        JTable table = new JTable(model);
        table.setFont(new Font("Tahoma", Font.PLAIN, 12));
        table.setRowHeight(24);
        body.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel totals = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totals.setBackground(new Color(248, 249, 250));
        JLabel totalLbl = new JLabel("Total: ₱" + String.format("%.2f", order.getTotalAmount()));
        totalLbl.setFont(new Font("Tahoma", Font.BOLD, 14));
        totals.add(totalLbl);
        body.add(totals, BorderLayout.SOUTH);

        add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(248, 249, 250));
        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(34, 156, 129));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }
}
