package hostel_management;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;

import hostel_management.AdminAccessWindow.StyledButton;

import java.sql.Types;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
public class ManagerAccessWindow extends JFrame {
    private Connection connection;

    public ManagerAccessWindow() {
        setTitle("Manager Window");
        
        // Create the main panel with a background image
        JPanel mainPanel = new BackgroundPanel(new GridBagLayout(), "./manager.jpeg");

        JButton viewReportsButton = createStyledButton("View Reports");
        JButton manageFacilitiesButton = createStyledButton("Manage Facilities");
        JButton viewCurrentResidentsButton = createStyledButton("View Current Residents");
        JButton viewSalariesAndDuesButton = createStyledButton("Financial Report");

        // Set icons for the buttons with a specific size
        viewReportsButton.setIcon(resizeImageIcon(new ImageIcon("./viewData.png"), 280, 280));
        manageFacilitiesButton.setIcon(resizeImageIcon(new ImageIcon("./facility.png"), 280, 280));
        viewCurrentResidentsButton.setIcon(resizeImageIcon(new ImageIcon("./viewProfile.png"), 280, 280));
        viewSalariesAndDuesButton.setIcon(resizeImageIcon(new ImageIcon("./dues.png"), 280, 280));

        add(mainPanel);
        viewReportsButton.addActionListener(e -> openViewReportsWindow());
        manageFacilitiesButton.addActionListener(e -> openManageFacilitiesWindow());
        viewSalariesAndDuesButton.addActionListener(e -> openSalariesAndDuesWindow());
        viewCurrentResidentsButton.addActionListener(e -> openViewCurrentResidentsWindow());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.ipadx = 5; // horizontal padding
        gbc.ipady = 5; // vertical padding

        mainPanel.add(viewReportsButton, gbc);
        gbc.gridx = 1;
        mainPanel.add(manageFacilitiesButton, gbc);
        gbc.gridx = 2;
        mainPanel.add(viewSalariesAndDuesButton, gbc);
        gbc.gridx = 3;
        mainPanel.add(viewCurrentResidentsButton, gbc);
       
        add(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1800, 850);
        setLocationRelativeTo(null);
        setVisible(true);

        connection = new DatabaseConnection().getConnection();
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

 private void openSalariesAndDuesWindow() {
     JFrame salariesAndDuesFrame = new JFrame("Salaries and Dues");
     salariesAndDuesFrame.getContentPane().setBackground(new Color(240, 240, 240)); // Light gray background

     // Create a panel to hold the content
     JPanel mainPanel = new JPanel();
     mainPanel.setLayout(new BorderLayout());
     mainPanel.setBackground(new Color(173, 216, 230)); // White background

     // Create a table to display salaries and dues
     JTable salariesAndDuesTable = new JTable();
     JScrollPane scrollPane = new JScrollPane(salariesAndDuesTable);
     mainPanel.add(scrollPane, BorderLayout.CENTER);

     try {
         // Populate data from the ResultSet to a DefaultTableModel
         DefaultTableModel tableModel = new DefaultTableModel();
         tableModel.setColumnIdentifiers(new Object[]{"ID", "Name", "Amount", "Type"});

         // Fetch salaries and dues from the database
         fetchSalariesAndDues(tableModel);

         salariesAndDuesTable.setModel(tableModel);

     } catch (SQLException ex) {
         ex.printStackTrace();
         // Handle SQL exception as needed
     }

     // Add labels for better clarity with a pleasing color
     JLabel titleLabel = new JLabel("Salaries and Dues Report");
     titleLabel.setFont(new Font("Open Sans", Font.BOLD, 24));
     titleLabel.setHorizontalAlignment(JLabel.CENTER);
     titleLabel.setForeground(new Color(70, 130, 180)); // Steel Blue color

     // Add title label to the top of the panel
     mainPanel.add(titleLabel, BorderLayout.NORTH);

     salariesAndDuesFrame.getContentPane().add(mainPanel);

     // Adjust the window size and location
     salariesAndDuesFrame.setSize(800, 600);
     salariesAndDuesFrame.setLocationRelativeTo(null);
     salariesAndDuesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
     salariesAndDuesFrame.setVisible(true);
 }


 private void fetchSalariesAndDues(DefaultTableModel tableModel) throws SQLException {
	    try (Connection connection = new DatabaseConnection().getConnection();
	         CallableStatement callableStatement = connection.prepareCall("{ call get_salaries_and_dues(?) }")) {
	    	callableStatement.registerOutParameter(1, Types.REF_CURSOR);

	        callableStatement.execute();

	        try (ResultSet resultSet = (ResultSet) callableStatement.getObject(1)) {
	            while (resultSet.next()) {
	                int id = resultSet.getInt("id");
	                String name = resultSet.getString("name");
	                double amount = resultSet.getDouble("amount");
	                String type = resultSet.getString("type");

	                tableModel.addRow(new Object[]{id, name, amount, type});
	            }
	        }
	    }
	}
 
    	 private void openViewReportsWindow() {
    		    JFrame viewReportsFrame = new JFrame("View Reports");
    		    viewReportsFrame.getContentPane().setBackground(new Color(30, 30, 70)); // Dark Blue background

    		    JPanel mainPanel = new JPanel();
    		    mainPanel.setLayout(new BorderLayout());
    		    mainPanel.setBackground(new Color(70, 130, 180)); // Steel Blue background

    		    JTable residentTable = new JTable();
    		    residentTable.setBackground(new Color(240, 248, 255)); // Alice Blue background
    		    residentTable.setSelectionBackground(new Color(173, 216, 230)); // Light Blue selection background

    		    JScrollPane scrollPane = new JScrollPane(residentTable);
    		    scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding around the table
    		    mainPanel.add(scrollPane, BorderLayout.CENTER);

    		    try {
    		        DefaultTableModel tableModel = new DefaultTableModel();
    		        tableModel.setColumnIdentifiers(new Object[]{"Resident ID", "First Name", "Last Name", "Gender",
    		                "Contact No", "Age", "Emergency Contact", "Address", "Address 2", "Room Number",
    		                "Check-In Date", "Check-Out Date"});

    		        try (Connection connection = new DatabaseConnection().getConnection();
    		             PreparedStatement preparedStatement = connection.prepareStatement(
    		                     "SELECT r.resident_id, r.first_name, r.last_name, r.gender, r.contact_num, r.age, " +
    		                             "r.emergency_cont, r.address, r.address2, r.room_number, " +
    		                             "c.check_in_date, cd.check_out_date " +
    		                             "FROM Residents r " +
    		                             "LEFT JOIN check_in c  ON r.resident_id = c.resident_id LEFT JOIN check_out cd ON r.resident_id = cd.resident_id order by resident_id");
    		             ResultSet resultSet = preparedStatement.executeQuery()) {

    		            while (resultSet.next()) {
    		                int residentId = resultSet.getInt("resident_id");
    		                String firstName = resultSet.getString("first_name");
    		                String lastName = resultSet.getString("last_name");
    		                String gender = resultSet.getString("gender");
    		                String contactNo = resultSet.getString("contact_num");
    		                int age = resultSet.getInt("age");
    		                String emergencyContact = resultSet.getString("emergency_cont");
    		                String address = resultSet.getString("address");
    		                String address2 = resultSet.getString("address2");
    		                int roomNumber = resultSet.getInt("room_number");
    		                Date checkInDate = resultSet.getDate("check_in_date");
    		                Date checkOutDate = resultSet.getDate("check_out_date");

    		                tableModel.addRow(new Object[]{residentId, firstName, lastName, gender, contactNo, age,
    		                        emergencyContact, address, address2, roomNumber, checkInDate, checkOutDate});
    		            }

    		        } catch (SQLException ex) {
    		            ex.printStackTrace();
    		            // Handle SQL exception as needed
    		        }

    		        residentTable.setModel(tableModel);

    		    } catch (Exception ex) {
    		        ex.printStackTrace();
    		    }

    		    viewReportsFrame.add(mainPanel);

    		    // Adjust the window size and location
    		    viewReportsFrame.setSize(1200, 800);
    		    viewReportsFrame.setLocationRelativeTo(null);
    		    viewReportsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		    viewReportsFrame.setVisible(true);
    		}
    	 private void openViewCurrentResidentsWindow() {
    		    JFrame viewCurrentResidentsFrame = new JFrame("View Current Residents");
    		    viewCurrentResidentsFrame.getContentPane().setBackground(new Color(30, 30, 70)); // Dark Blue background

    		    JPanel mainPanel = new JPanel();
    		    mainPanel.setLayout(new BorderLayout());
    		    mainPanel.setBackground(new Color(70, 130, 180)); // Steel Blue background

    		    JTable currentResidentsTable = new JTable();
    		    currentResidentsTable.setBackground(new Color(240, 248, 255)); // Alice Blue background
    		    currentResidentsTable.setSelectionBackground(new Color(173, 216, 230)); // Light Blue selection background

    		    JScrollPane scrollPane = new JScrollPane(currentResidentsTable);
    		    scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding around the table
    		    mainPanel.add(scrollPane, BorderLayout.CENTER);

    		    try {
    		        DefaultTableModel tableModel = new DefaultTableModel();
    		        tableModel.setColumnIdentifiers(new Object[]{"Resident ID", "First Name", "Last Name", "Gender",
    		                "Contact No", "Age", "Emergency Contact", "Address", "Address 2", "Room Number"});

    		        try (Connection connection = new DatabaseConnection().getConnection();
    		             PreparedStatement preparedStatement = connection.prepareStatement(
    		                     "SELECT resident_id, first_name, last_name, gender, contact_num, age, " +
    		                             "emergency_cont, address, address2, room_number " +
    		                             "FROM CurrentResidentsView");
    		             ResultSet resultSet = preparedStatement.executeQuery()) {

    		            while (resultSet.next()) {
    		                int residentId = resultSet.getInt("resident_id");
    		                String firstName = resultSet.getString("first_name");
    		                String lastName = resultSet.getString("last_name");
    		                String gender = resultSet.getString("gender");
    		                String contactNo = resultSet.getString("contact_num");
    		                int age = resultSet.getInt("age");
    		                String emergencyContact = resultSet.getString("emergency_cont");
    		                String address = resultSet.getString("address");
    		                String address2 = resultSet.getString("address2");
    		                int roomNumber = resultSet.getInt("room_number");

    		                tableModel.addRow(new Object[]{residentId, firstName, lastName, gender, contactNo, age,
    		                        emergencyContact, address, address2, roomNumber});
    		            }

    		        } catch (SQLException ex) {
    		            ex.printStackTrace();
    		            // Handle SQL exception as needed
    		        }

    		        currentResidentsTable.setModel(tableModel);

    		    } catch (Exception ex) {
    		        ex.printStackTrace();
    		    }

    		    viewCurrentResidentsFrame.add(mainPanel);

    		    // Adjust the window size and location
    		    viewCurrentResidentsFrame.setSize(1200,800);
    		    viewCurrentResidentsFrame.setLocationRelativeTo(null);
    		    viewCurrentResidentsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		    viewCurrentResidentsFrame.setVisible(true);
    		}
    private void openManageFacilitiesWindow() {
        JFrame manageFacilitiesFrame = new JFrame("Manage Facilities");
        manageFacilitiesFrame.getContentPane().setBackground(new Color(30, 30, 70)); // Dark Blue background

        // Add logic and components for managing hostel facilities
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(70, 130, 180)); // Steel Blue background

        // Create a table to display the results
        JTable facilityTable = new JTable();

        JScrollPane scrollPane = new JScrollPane(facilityTable);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding around the table
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        try {
            // Populate data from the ResultSet to a DefaultTableModel
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(new Object[]{"Facility ID", "Facility Name", "Facility Detail"});

            // Fetch facilities from the database
            fetchFacilities(tableModel);

            facilityTable.setModel(tableModel);

            // Add buttons for adding and deleting facilities
            JButton addButton = new JButton("Add Facility");
            JButton deleteButton = new JButton("Delete Facility");

            addButton.addActionListener(e -> addFacility(tableModel));
            deleteButton.addActionListener(e -> deleteFacility(facilityTable, tableModel));

            // Set button colors to brown
            addButton.setBackground(new Color(139, 69, 19)); // Brown color
            deleteButton.setBackground(new Color(139, 69, 19)); // Brown color
            addButton.setForeground(Color.WHITE);
            deleteButton.setForeground(Color.WHITE);

            // Create a panel for the buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(new Color(70, 130, 180)); // Steel Blue background
            buttonPanel.add(addButton);
            buttonPanel.add(deleteButton);

            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Add a selection listener to the table
            facilityTable.getSelectionModel().addListSelectionListener(e -> {
                int selectedRow = facilityTable.getSelectedRow();
                deleteButton.setEnabled(selectedRow >= 0);
            });

        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle SQL exception as needed
        }

        manageFacilitiesFrame.getContentPane().add(mainPanel);

        // Adjust the window size and location
        manageFacilitiesFrame.setSize(600, 400);
        manageFacilitiesFrame.setLocationRelativeTo(null);
        manageFacilitiesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        manageFacilitiesFrame.setVisible(true);
    }

    private void fetchFacilities(DefaultTableModel tableModel) throws SQLException {
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Facility");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int facilityId = resultSet.getInt("facility_id");
                String facilityName = resultSet.getString("facility_name");
                String facilityDetail = resultSet.getString("facility_detail");
                tableModel.addRow(new Object[]{facilityId, facilityName, facilityDetail});
            }
        }
    }

    private void addFacility(DefaultTableModel tableModel) {
        String facilityName = JOptionPane.showInputDialog("Enter Facility Name:");
        String facilityDetail = JOptionPane.showInputDialog("Enter Facility Detail:");

        if (facilityName != null && facilityDetail != null) {
            try (Connection connection = new DatabaseConnection().getConnection()) {
                // Insert facility information into Facility table
                try (PreparedStatement insertFacilityStatement = connection.prepareStatement(
                        "INSERT INTO Facility (facility_id, facility_name, facility_detail) VALUES (facility_seq.NEXTVAL, ?, ?)")) {
                    insertFacilityStatement.setString(1, facilityName);
                    insertFacilityStatement.setString(2, facilityDetail);

                    int rowsAffected = insertFacilityStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Facility added successfully.");

                        // Refresh the table to reflect the changes
                        tableModel.setRowCount(0);
                        fetchFacilities(tableModel);

                        // Calculate the expense amount based on the added facility (example logic)
                        double expenseAmount = calculateExpenseAmountForFacility(facilityName);

                        // Add corresponding expense entry in the Expenses table
                        try (PreparedStatement insertExpenseStatement = connection.prepareStatement(
                                "INSERT INTO Expenses (description, amount, expense_date) VALUES (?, ?, CURRENT_DATE)")) {
                            insertExpenseStatement.setString(1, "Facility: " + facilityName);
                            insertExpenseStatement.setDouble(2, expenseAmount);

                            insertExpenseStatement.executeUpdate();
                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add facility.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Handle SQL exception as needed
            }
        }
    }

    private double calculateExpenseAmountForFacility(String facilityName) {
        // Replace this with your actual logic to calculate the expense amount based on the facility
        // For demonstration, let's assume different calculations for different types of facilities

        double baseAmount = 10000.0; // Default amount for facilities

        if (facilityName.equals("Swimming Pool")) {
            return baseAmount + 5000.0; // Additional cost for a swimming pool
        } else if (facilityName.equals("Gym")) {
            return baseAmount + 3000.0; // Additional cost for a gym
        }  else if (facilityName.equals("Mess")) {
            return baseAmount + 8000.0; // Additional cost for a gym
        } else {
            return baseAmount; // Default amount for other facilities
        }
    }


    private void deleteFacility(JTable facilityTable, DefaultTableModel tableModel) {
        int selectedRow = facilityTable.getSelectedRow();

        // Check if a row is selected
        if (selectedRow >= 0 && selectedRow < tableModel.getRowCount()) {
            int facilityId = (int) tableModel.getValueAt(selectedRow, 0);

            int confirmDialogResult = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete this facility?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirmDialogResult == JOptionPane.YES_OPTION) {
                try (Connection connection = new DatabaseConnection().getConnection()) {
                    // Remove facility information from Facility table
                    try (PreparedStatement deleteFacilityStatement = connection
                            .prepareStatement("DELETE FROM Facility WHERE facility_id = ?")) {
                        deleteFacilityStatement.setInt(1, facilityId);

                        int rowsAffected = deleteFacilityStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(null, "Facility deleted successfully.");

                            // Remove corresponding expense entry from the Expenses table
                            try (PreparedStatement deleteExpenseStatement = connection.prepareStatement(
                                    "DELETE FROM Expenses WHERE description = ?")) {
                                deleteExpenseStatement.setString(1,
                                        "Facility: " + tableModel.getValueAt(selectedRow, 1)); // Assuming column 1 is the facility name

                                int expenseRowsAffected = deleteExpenseStatement.executeUpdate();

                                if (expenseRowsAffected > 0) {
                                    // Refresh the table to reflect the changes
                                    tableModel.removeRow(selectedRow);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Failed to delete expense entry.", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to delete facility.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    // Handle SQL exception as needed
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a facility to delete.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManagerAccessWindow());
    }
}