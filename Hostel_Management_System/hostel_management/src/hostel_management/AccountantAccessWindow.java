package hostel_management;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;

import hostel_management.AdminAccessWindow.StyledButton;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class AccountantAccessWindow extends JFrame {

    private Connection connection;

    public AccountantAccessWindow() {
        setTitle("Accountant Window");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connection = new DatabaseConnection().getConnection();

        createAndShowGUI();
    }

    private void createAndShowGUI() {
    	setTitle("Accountant Window");
    	
        JPanel mainPanel = new BackgroundPanel(new GridBagLayout(), "./accountant.jpg");

        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Accountant Window");
        titleLabel.setFont(new Font("Open Sans", Font.BOLD, 32));
        titlePanel.setAlignmentX(CENTER_ALIGNMENT);
        titlePanel.setAlignmentY(CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);


        JButton manageDuesButton = createStyledButton("Manage Dues");
        JButton manageSalariesButton = createStyledButton("Manage Salaries");
        JButton manageExpensesButton = createStyledButton("Manage Expenses");
        JButton generateReportButton = createStyledButton("Generate Financial Report");
        
        manageDuesButton.setIcon(resizeImageIcon(new ImageIcon("./dues.png"), 280, 280));
        manageSalariesButton.setIcon(resizeImageIcon(new ImageIcon("./Salary.png"), 280, 280));
        manageExpensesButton.setIcon(resizeImageIcon(new ImageIcon("./viewReport.png"), 280, 280));
        generateReportButton.setIcon(resizeImageIcon(new ImageIcon("./viewData.png"), 280, 280));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.ipadx = 5; // horizontal padding
        gbc.ipady = 5; // vertical padding

        mainPanel.add(manageDuesButton, gbc);
        gbc.gridx = 1;
        mainPanel.add(manageSalariesButton, gbc);
        gbc.gridx = 2;
        mainPanel.add(manageExpensesButton, gbc);
        gbc.gridx = 3;
        mainPanel.add(generateReportButton, gbc);
       
        add(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1800, 850);
        setLocationRelativeTo(null);
        setVisible(true);

        manageDuesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openManageDuesWindow();
            }
        });

        manageSalariesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openManageSalariesWindow();
            }
        });

        manageExpensesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openManageExpensesWindow();
            }
        });

        generateReportButton.addActionListener(e -> generateFinancialReport());
    
    }
    private ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }
    
     JButton createStyledButton(String text) {
        JButton button = new StyledButton(text);

        // Set the vertical text position to appear below the image
        button.setVerticalTextPosition(SwingConstants.BOTTOM);

        // Set the horizontal alignment of the text to center
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        // Remove border lines from the button
        button.setBorderPainted(false);

        // Make the content area of the button transparent
        button.setBackground(null);

        // Disable the focus-painted effect (removes default blue color on click)
        button.setFocusPainted(false);
        
        // Set the button's content area to be non-opaque
        button.setOpaque(false);

        // Set the UI to BasicButtonUI for consistent appearance
        button.setUI(new BasicButtonUI());

        // Set the font size and color
        Font customFont = new Font("Times New Roman", Font.BOLD, 21); // Customize the font as needed
        button.setFont(customFont);
        button.setForeground(Color.WHITE); // Set your desired text color

        return button;
    }

    
     static class StyledButton extends JButton {
        private boolean isHovered = false;

        public StyledButton(String text) {
            super(text);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (isHovered) {
                // Draw a subtle drop shadow when hovered
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRect(3, 3, getWidth() - 6, getHeight() - 6);
                g2d.dispose();
            }

            // Paint the button content as usual
            super.paintComponent(g);
        }
    }
     
     private void openManageDuesWindow() {
         JFrame manageDuesFrame = new JFrame("Manage Dues");

         JPanel mainPanel = createMainPanel();

         try {
             DefaultTableModel tableModel = new DefaultTableModel();
             tableModel.setColumnIdentifiers(new Object[]{"Resident ID", "Dues Amount", "Payment Status"});

             fetchDues(tableModel);

             JTable duesTable = new JTable(tableModel);
             JScrollPane scrollPane = new JScrollPane(duesTable);
             mainPanel.add(scrollPane, BorderLayout.CENTER);

             JButton updatePaymentButton = createUpdatePaymentButton(duesTable, tableModel);
             JPanel buttonPanel = createButtonPanel(updatePaymentButton);
             mainPanel.add(buttonPanel, BorderLayout.SOUTH);

         } catch (SQLException ex) {
             ex.printStackTrace();
             JOptionPane.showMessageDialog(null, "Failed to fetch dues.",
                     "Error", JOptionPane.ERROR_MESSAGE);
         }

         decorateManageDuesFrame(manageDuesFrame, mainPanel);
     }

     private JPanel createMainPanel() {
         JPanel mainPanel = new JPanel(new BorderLayout());
         mainPanel.setBackground(new Color(96, 179, 209));

         // Create a rounded border for the main panel
         LineBorder roundedLineBorder = new LineBorder(new Color(96, 179, 209), 2, true);
         TitledBorder roundedTitledBorder = new TitledBorder(roundedLineBorder, "Manage Dues");
         mainPanel.setBorder(roundedTitledBorder);

         return mainPanel;
     }

     private JButton createUpdatePaymentButton(JTable duesTable, DefaultTableModel tableModel) {
    	    JButton updatePaymentButton = new JButton("Update Payment Status");

    	    // Set a darker shade of blue for the button background
    	    Color buttonColor = new Color(30, 103, 164);
    	    updatePaymentButton.setBackground(buttonColor);
    	    updatePaymentButton.setForeground(Color.WHITE);

    	    updatePaymentButton.addActionListener(e -> updatePaymentStatus(duesTable, tableModel));

    	    return updatePaymentButton;
    	}

     private JPanel createButtonPanel(JButton updatePaymentButton) {
         JPanel buttonPanel = new JPanel();
         buttonPanel.setBackground(new Color(96, 179, 209));
         buttonPanel.add(updatePaymentButton);

         return buttonPanel;
     }
     private void decorateManageDuesFrame(JFrame manageDuesFrame, JPanel mainPanel) {
         manageDuesFrame.getContentPane().add(mainPanel);
         manageDuesFrame.setSize(800, 600);
         manageDuesFrame.setLocationRelativeTo(null);
         manageDuesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         manageDuesFrame.setVisible(true);
     }
     
    private void fetchDues(DefaultTableModel tableModel) throws SQLException {
        try (Connection connection = new DatabaseConnection().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT resident_id, due_amount, payment_status FROM Dues")) {

            while (resultSet.next()) {
                int residentId = resultSet.getInt("resident_id");
                double duesAmount = resultSet.getDouble("due_amount");
                String paymentStatus = resultSet.getString("payment_status");

                tableModel.addRow(new Object[]{residentId, duesAmount, paymentStatus});
            }
        }
    }

    private void updatePaymentStatus(JTable duesTable, DefaultTableModel tableModel) {
        int selectedRow = duesTable.getSelectedRow();
        int residentId = (int) tableModel.getValueAt(selectedRow, 0);

        String paymentStatus = (String) tableModel.getValueAt(selectedRow, tableModel.getColumnCount() - 1);

        if (!"Paid".equalsIgnoreCase(paymentStatus)) {
            try (Connection connection = new DatabaseConnection().getConnection();
                 PreparedStatement updateStatement = connection.prepareStatement(
                         "UPDATE Dues SET payment_status = 'Paid' WHERE resident_id = ?")) {
                updateStatement.setInt(1, residentId);
                updateStatement.executeUpdate();

                // Update the tableModel and refresh the table
                tableModel.setValueAt("Paid", selectedRow, tableModel.getColumnCount() - 1);
                JOptionPane.showMessageDialog(null, "Payment status updated to 'Paid'.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to update payment status.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Payment status is already 'Paid'.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
   

    private void openManageSalariesWindow() {
        JFrame manageSalariesFrame = new JFrame("Manage Salaries");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));  // Set a light background color

        JTable salariesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(salariesTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        try {
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(new Object[]{"Employee ID", "Employee Name", "Salary", "Work Years"});

            fetchSalariesAndWorkYears(tableModel);

            salariesTable.setModel(tableModel);

            JButton updateSalaryButton = createUpdateSalaryButton(salariesTable, tableModel);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(updateSalaryButton);

            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch salaries and work years.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        manageSalariesFrame.getContentPane().add(mainPanel);
        manageSalariesFrame.setSize(600, 400);
        manageSalariesFrame.setLocationRelativeTo(null);
        manageSalariesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        manageSalariesFrame.setVisible(true);
    }

    private JButton createUpdateSalaryButton(JTable salariesTable, DefaultTableModel tableModel) {
        JButton updateSalaryButton = new JButton("Update Salary");

        // Set a darker shade of blue for the button background
        Color buttonColor = new Color(30, 103, 164);
        updateSalaryButton.setBackground(buttonColor);
        updateSalaryButton.setForeground(Color.WHITE);

        updateSalaryButton.addActionListener(e -> updateSalary(salariesTable, tableModel));

        return updateSalaryButton;
    }

    private void fetchSalariesAndWorkYears(DefaultTableModel tableModel) throws SQLException {
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT employeeID, firstName, lastName, salary, TRUNC(MONTHS_BETWEEN(SYSDATE, registrationDate) / 12) AS workYears FROM employeesP")) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int employeeId = resultSet.getInt("employeeID");
                String employeeName = resultSet.getString("firstName") + " " + resultSet.getString("lastName");
                double salary = resultSet.getDouble("salary");
                int workYears = resultSet.getInt("workYears");

                tableModel.addRow(new Object[]{employeeId, employeeName, salary, workYears});
            }
        }
    }

    private void updateSalary(JTable salariesTable, DefaultTableModel tableModel) {
        int selectedRow = salariesTable.getSelectedRow();
        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);

        String newSalaryString = JOptionPane.showInputDialog("Enter New Salary:");

        if (newSalaryString != null) {
            try {
                double newSalary = Double.parseDouble(newSalaryString);

                try (Connection connection = new DatabaseConnection().getConnection();
                     PreparedStatement updateStatement = connection.prepareStatement(
                             "UPDATE employeesP SET salary = ? WHERE employeeID = ?")) {
                    updateStatement.setDouble(1, newSalary);
                    updateStatement.setInt(2, employeeId);

                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Salary updated successfully.");
                        tableModel.setRowCount(0);
                        fetchSalariesAndWorkYears(tableModel);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update salary.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException | SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid values.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 // Add the following method to your AccountantAccessWindow class

    private void openManageExpensesWindow() {
        JFrame manageExpensesFrame = new JFrame("Manage Expenses");
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(96, 179, 209)); // Set the background color

        JTable expensesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(expensesTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        try {
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(new Object[]{"Expense ID", "Description", "Amount", "Expense Date"});

            fetchExpenses(tableModel);

            expensesTable.setModel(tableModel);

            JButton addExpenseButton = createStyleButton("Add Expense");
            JButton deleteExpenseButton = createStyleButton("Delete Expense");

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(addExpenseButton);
            buttonPanel.add(deleteExpenseButton);
            buttonPanel.setOpaque(false); // Make the button panel transparent

            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            addExpenseButton.addActionListener(e -> addExpense(tableModel));
            deleteExpenseButton.addActionListener(e -> deleteExpense(expensesTable, tableModel));

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch expenses.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        manageExpensesFrame.getContentPane().add(mainPanel);
        manageExpensesFrame.setSize(600, 400);
        manageExpensesFrame.setLocationRelativeTo(null);
        manageExpensesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        manageExpensesFrame.setVisible(true);
    }

    private JButton createStyleButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(30, 103, 164)); // Set a darker shade of blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false); // Remove the button border
        button.setFont(new Font("Arial", Font.PLAIN, 14)); // Set the font

        return button;
    }
    private void addExpense(DefaultTableModel tableModel) {
        JTextField descriptionField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField dateField = new JTextField();

        Object[] fields = {"Description:", descriptionField, "Amount:", amountField, "Expense Date (YYYY-MM-DD):", dateField};

        int result = JOptionPane.showConfirmDialog(null, fields, "Add Expense", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String description = descriptionField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String date = dateField.getText();

                // Implement logic to add the expense to the database
                try (Connection connection = new DatabaseConnection().getConnection();
                     PreparedStatement insertStatement = connection.prepareStatement(
                             "INSERT INTO Expenses (description, amount, expense_date) VALUES (?, ?, ?)")) {
                    insertStatement.setString(1, description);
                    insertStatement.setDouble(2, amount);
                    insertStatement.setDate(3, Date.valueOf(date));

                    int rowsAffected = insertStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Expense added successfully.");
                        tableModel.setRowCount(0); // Clear the table
                        fetchExpenses(tableModel); // Refresh the table
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add expense.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (NumberFormatException | SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid values.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void fetchExpenses(DefaultTableModel tableModel) throws SQLException {
        try (Connection connection = new DatabaseConnection().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Expenses")) {

            while (resultSet.next()) {
                int expenseId = resultSet.getInt("expense_id");
                String description = resultSet.getString("description");
                double amount = resultSet.getDouble("amount");
                Date expenseDate = resultSet.getDate("expense_date");

                tableModel.addRow(new Object[]{expenseId, description, amount, expenseDate});
            }
        }
    }

    private void deleteExpense(JTable expensesTable, DefaultTableModel tableModel) {
        int selectedRow = expensesTable.getSelectedRow();

        if (selectedRow != -1) {
            int expenseId = (int) tableModel.getValueAt(selectedRow, 0);

            // Implement logic to delete the expense from the database
            try (Connection connection = new DatabaseConnection().getConnection();
                 PreparedStatement deleteStatement = connection.prepareStatement(
                         "DELETE FROM Expenses WHERE expense_id = ?")) {
                deleteStatement.setInt(1, expenseId);

                int rowsAffected = deleteStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Expense deleted successfully.");
                    tableModel.removeRow(selectedRow); // Remove the row from the table model
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete expense.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to delete expense.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select an expense to delete.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

 // Add the following method to your AccountantAccessWindow class
    private void generateFinancialReport() {
        try (Connection connection = new DatabaseConnection().getConnection();
             Statement incomeStatement = connection.createStatement();
             Statement expenseStatement = connection.createStatement();
             Statement salaryStatement = connection.createStatement()) {

            // Calculate Total Income for Paid Dues
            ResultSet incomeResultSet = incomeStatement.executeQuery("SELECT SUM(due_amount) AS total_income FROM Dues WHERE payment_status = 'Paid'");
            double totalIncome = 0;
            if (incomeResultSet.next()) {
                totalIncome = incomeResultSet.getDouble("total_income");
            }

            // Calculate Total Expenses
            ResultSet expenseResultSet = expenseStatement.executeQuery("SELECT SUM(amount) AS total_expenses FROM Expenses");
            double totalExpenses = 0;
            if (expenseResultSet.next()) {
                totalExpenses = expenseResultSet.getDouble("total_expenses");
            }

            // Calculate Total Salaries
            ResultSet salaryResultSet = salaryStatement.executeQuery("SELECT SUM(salary) AS total_salaries FROM employeesP");
            double totalSalaries = 0;
            if (salaryResultSet.next()) {
                totalSalaries = salaryResultSet.getDouble("total_salaries");
            }

            // Calculate Net Expenses (Expenses + Salaries)
            double netExpenses = totalExpenses + totalSalaries;

            // Calculate Net Profit
            double netProfit = totalIncome - netExpenses;

            // Display Financial Report
            String reportMessage = String.format(
                    "Total Income from Paid Dues: $%.2f\nTotal Expenses (including salaries): $%.2f\nNet Profit: $%.2f", totalIncome, netExpenses, netProfit);

            JOptionPane.showMessageDialog(null, reportMessage, "Financial Report", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to generate financial report.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AccountantAccessWindow());
    }
    
}