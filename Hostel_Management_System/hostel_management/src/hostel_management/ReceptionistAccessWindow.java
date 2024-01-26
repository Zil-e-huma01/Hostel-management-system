package hostel_management;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import hostel_management.AdminAccessWindow.StyledButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class ReceptionistAccessWindow extends JFrame {

    private Connection connection;
    private JTextField cnicField;
    private JButton searchButton;
    private JButton confirmCheckoutButton;
    JPanel residentDataPanel = new JPanel(new GridLayout(8, 2));

    public ReceptionistAccessWindow() {
        setTitle("Receptionist Window");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connection = new DatabaseConnection().getConnection();
        
        createAndShowGUI();
    }

    private void createAndShowGUI() {
    	setTitle("Receptionist Window");

        JPanel mainPanel = new BackgroundPanel(new GridBagLayout(), "./receptionist.jpeg");
        
        JButton addResidentsButton = createStyledButton("Add Residents");
        JButton checkOutButton = createStyledButton("Check-Out");
        JButton RoomsAvailable = createStyledButton("Rooms Availble");
              
        // Set icons for the buttons with a specific size
        addResidentsButton.setIcon(resizeImageIcon(new ImageIcon("./addResident.png"), 280, 280));
        checkOutButton.setIcon(resizeImageIcon(new ImageIcon("./checkOut.png"), 280, 280));
        RoomsAvailable.setIcon(resizeImageIcon(new ImageIcon("./rooms.png"), 280, 280));


        addResidentsButton.addActionListener(e -> openAddResidentsWindow());
        checkOutButton.addActionListener(e -> openCheckoutWindow());
        RoomsAvailable.addActionListener(e -> showAvailableRooms());

        // Add rentField and confirmCheckoutButton to the checkoutPanel or another relevant container
        JPanel checkoutPanel = new JPanel(); // You need to define checkoutPanel
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.ipadx = 5; // horizontal padding
        gbc.ipady = 5; // vertical padding

        mainPanel.add(addResidentsButton, gbc);
        gbc.gridx = 1;
        mainPanel.add(checkOutButton, gbc);
        gbc.gridx = 2;
        mainPanel.add(RoomsAvailable, gbc);

       
        add(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1800, 850);
        setLocationRelativeTo(null);
        setVisible(true);
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
     
 

    private boolean isResidentCheckedIn(int residentId) throws SQLException {
    	System.out.println("Checking if resident with ID " + residentId + " exists.");
    	
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM check_out  WHERE resident_id = ? AND check_out_date IS NULL")) {
            preparedStatement.setInt(1, residentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private boolean isResidentIdValid(int residentId) throws SQLException {
 	   try (PreparedStatement preparedStatement = connection.prepareStatement(
 	           "SELECT * FROM Residents WHERE resident_id = ?")) {
 	       preparedStatement.setInt(1, residentId);
 	       
 	       try (ResultSet resultSet = preparedStatement.executeQuery()) {
 	    	   
 	           return resultSet.next();
 	       }
 	   }
 	}
    
    private void openAddResidentsWindow() {
    	JFrame addResidentFrame = new JFrame("Add Resident Form");
        
        JPanel addResidentPanel = new BackgroundPanel(new GridLayout(0, 2), "./receptionist.webp.jpeg");
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField firstNameField = new JTextField();
        firstNameField.setPreferredSize(new Dimension(200, 25));
        
        JTextField lastNameField = new JTextField();
        lastNameField.setPreferredSize(new Dimension(200, 25));
        
        JPanel genderPanel = new JPanel();
        JRadioButton maleRadioButton = new JRadioButton("Male");
        JRadioButton femaleRadioButton = new JRadioButton("Female");

        // Group the radio buttons to ensure only one can be selected
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadioButton);
        genderGroup.add(femaleRadioButton);

        genderPanel.add(maleRadioButton);
        genderPanel.add(femaleRadioButton);
        formPanel.add(genderPanel); 
        
        JTextField contactNoField = new JTextField();
        contactNoField.setPreferredSize(new Dimension(200, 25));
        
        JTextField ageField = new JTextField();
        ageField.setPreferredSize(new Dimension(200, 25));
        
        JTextField emergencyContactField = new JTextField();
        emergencyContactField.setPreferredSize(new Dimension(200, 25));
        
        JTextField cnicField = new JTextField();
        cnicField.setPreferredSize(new Dimension(200, 25));

        JTextField addressField = new JTextField();
        addressField.setPreferredSize(new Dimension(200, 25));

        JTextField address2Field = new JTextField();
        address2Field.setPreferredSize(new Dimension(200, 25));

        JTextField roomNumberField = new JTextField();
        roomNumberField.setPreferredSize(new Dimension(200, 25));

        RoundedButton submitButton = new RoundedButton("Submit", 110, 26);
        submitButton.setPreferredSize(new Dimension(130, 50));    
        
        addComponent(formPanel, new JLabel("First Name:"), gbc, 0, 0);
        addComponent(formPanel, firstNameField, gbc, 1, 0);

        addComponent(formPanel, new JLabel("Last Name:"), gbc, 0, 1);
        addComponent(formPanel, lastNameField, gbc, 1, 1);

        addComponent(formPanel, new JLabel("Gender:"), gbc, 0, 2);
        addComponent(formPanel, genderPanel, gbc, 1, 2);

        addComponent(formPanel, new JLabel("Contact No (Format: 12345678901):"), gbc, 0, 3);
        addComponent(formPanel, contactNoField, gbc, 1, 3);

        addComponent(formPanel, new JLabel("Age:"), gbc, 0, 4);
        addComponent(formPanel, ageField, gbc, 1, 4);

        addComponent(formPanel, new JLabel("Emergency Contact (Format: 12345678901):"), gbc, 0, 5);
        addComponent(formPanel, emergencyContactField, gbc, 1, 5);

        addComponent(formPanel, new JLabel("CNIC (Format: 12345-1234567-1):"), gbc, 0, 6);
        addComponent(formPanel, cnicField, gbc, 1, 6);

        addComponent(formPanel, new JLabel("Address:"), gbc, 0, 7);
        addComponent(formPanel, addressField, gbc, 1, 7);

        addComponent(formPanel, new JLabel("Address 2:"), gbc, 0, 8);
        addComponent(formPanel, address2Field, gbc, 1, 8);

        addComponent(formPanel, new JLabel("Room Number:"), gbc, 0, 9);
        addComponent(formPanel, roomNumberField, gbc, 1, 9);
        
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        addComponent(formPanel, new JLabel(""), gbc, 0, 10);
        
        gbc.gridwidth = GridBagConstraints.RELATIVE;  // Make the Submit button span the remaining columns
        addComponent(formPanel, submitButton, gbc, 0, 11);
        
        formPanel.add(submitButton);

        submitButton.addActionListener(e -> {
            if (validateResidentForm( firstNameField, lastNameField, maleRadioButton, femaleRadioButton,
                    contactNoField, ageField, emergencyContactField, addressField, address2Field, roomNumberField)) {
                addResidentData( firstNameField.getText(), lastNameField.getText(),
                        getSelectedGender(maleRadioButton, femaleRadioButton),
                        contactNoField.getText(), Integer.parseInt(ageField.getText()),
                        emergencyContactField.getText(), addressField.getText(),
                        address2Field.getText(), Integer.parseInt(roomNumberField.getText()), cnicField.getText());

                addResidentFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(addResidentFrame, "Invalid data. Please check the form.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addResidentPanel.add(formPanel); // Add formPanel to addEmployeePanel

        addResidentFrame.add(addResidentPanel);
        addResidentFrame.setSize(1800, 850);
        addResidentFrame.setLocationRelativeTo(null);
        addResidentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only the addEmployeeFrame
        addResidentFrame.setVisible(true);

    }
    
    private void addComponent(Container container, Component component, GridBagConstraints gbc, int gridx, int gridy) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        container.add(component, gbc);
    }

    private boolean validateResidentForm( JTextField firstNameField, JTextField lastNameField,
            JRadioButton maleRadioButton, JRadioButton femaleRadioButton,
            JTextField contactNoField, JTextField ageField,
            JTextField emergencyContactField, JTextField addressField,
            JTextField address2Field, JTextField roomNumberField) {
    			// Check if required fields are not empty
    		if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
    					contactNoField.getText().isEmpty() || ageField.getText().isEmpty() || emergencyContactField.getText().isEmpty() ||
    					addressField.getText().isEmpty() || roomNumberField.getText().isEmpty()) {
    			JOptionPane.showMessageDialog(null, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
    			return false;
    		}
    		
// Check if a gender is selected
if (!maleRadioButton.isSelected() && !femaleRadioButton.isSelected()) {
JOptionPane.showMessageDialog(null, "Please select a gender.", "Error", JOptionPane.ERROR_MESSAGE);
return false;
}

// Check if age is a valid number
try {
int age = Integer.parseInt(ageField.getText());
if (age < 0) {
JOptionPane.showMessageDialog(null, "Please enter a valid age.", "Error", JOptionPane.ERROR_MESSAGE);
return false;
}
} catch (NumberFormatException e) {
JOptionPane.showMessageDialog(null, "Please enter a valid age.", "Error", JOptionPane.ERROR_MESSAGE);
return false;
}

// Check if room number is a valid number
try {
int roomNumber = Integer.parseInt(roomNumberField.getText());
if (roomNumber < 1 || roomNumber > 30) {
JOptionPane.showMessageDialog(null, "Please enter a valid room number (1-30).", "Error", JOptionPane.ERROR_MESSAGE);
return false;
}
} catch (NumberFormatException e) {
JOptionPane.showMessageDialog(null, "Please enter a valid room number.", "Error", JOptionPane.ERROR_MESSAGE);
return false;
}

// All checks passed, form is valid
return true;
}


    private String getSelectedGender(JRadioButton maleRadioButton, JRadioButton femaleRadioButton) {
        if (maleRadioButton.isSelected()) {
            return "Male";
        } else if (femaleRadioButton.isSelected()) {
            return "Female";
        } else {
            return "";
        }
    }
  
    private void addResidentData(String firstName, String lastName, String gender,
            String contactNo, int age, String emergencyContact,
            String address, String address2, int roomNumber, String cnic) {
        boolean success = false;

        try (Connection connection = new DatabaseConnection().getConnection()) {
            // Prepare the statement outside the try-with-resources to catch specific exceptions
            PreparedStatement preparedStatement = connection.prepareStatement(
            		"INSERT INTO Residents (first_name, last_name, gender, contact_num, age, emergency_cont, address, address2, room_number, cnic) " +
            				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, gender);
            preparedStatement.setString(4, contactNo);
            
            preparedStatement.setInt(5, age);
            preparedStatement.setString(6, emergencyContact);
            preparedStatement.setString(7, address);
            preparedStatement.setString(8, address2);
            preparedStatement.setInt(9, roomNumber);
            preparedStatement.setString(10, cnic);
            
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                success = true;
            } else {
                System.err.println("Failed to add resident data.");
            }
        } catch (SQLException ex) {
            // Handle SQL exceptions
            ex.printStackTrace();
            showError("Failed to add resident data. " + ex.getMessage());
        }

        // Display success message only if the operation was successful
        if (success) {
            JOptionPane.showMessageDialog(null, "Resident data added successfully.");
        }
    }




    private void handleOperationError(Exception ex, String errorMessage) {
        ex.printStackTrace();
        showError(errorMessage);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private interface CheckOperation {
        void perform(int residentId, Date checkDate) throws SQLException;
    }
    
    ////////////////////////////
    //CHECK OUT RECORD BUTTON
    ///////////////////////////

    private void openCheckoutWindow() {
        JFrame checkoutFrame = new JFrame("Checkout Form");

        // Main panel with GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(96, 179, 209));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Create a white container panel with rounded corners
        JPanel checkoutPanel = new JPanel(new GridLayout(5, 2)); // Reduced the number of rows
        checkoutPanel.setBackground(Color.WHITE);
        checkoutPanel.setPreferredSize(new Dimension(500, 200)); // Adjusted the size of the panel

        // Create a rounded border for the white container
        LineBorder roundedLineBorder = new LineBorder(new Color(96, 179, 209), 2, true);
        TitledBorder roundedTitledBorder = new TitledBorder(roundedLineBorder, "Checkout Form");
        checkoutPanel.setBorder(roundedTitledBorder);

        // Add empty space at the top
        checkoutPanel.add(new JLabel(""));
        checkoutPanel.add(new JLabel("Enter CNIC"));

        // CNIC field with placeholder
        JTextField cnicField = new JTextField();
        cnicField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cnicField.setMargin(new Insets(2, 2, 2, 2));
        cnicField.setFont(new Font("Serif", Font.PLAIN, 16));
        cnicField.setForeground(Color.GRAY);
        cnicField.setText("Enter CNIC");
        cnicField.setBorder(BorderFactory.createLineBorder(new Color(96, 179, 209)));

        // Add focus listener to handle placeholder behavior
        cnicField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (cnicField.getText().equals("Enter CNIC")) {
                    cnicField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (cnicField.getText().isEmpty()) {
                    cnicField.setText("Enter CNIC");
                }
            }
        });

        checkoutPanel.add(cnicField);

        // Confirm Checkout button (centered)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        checkoutPanel.add(new JLabel(""));
        RoundedButton confirmCheckoutButton = new RoundedButton("Confirm Checkout", 160, 35);
        confirmCheckoutButton.addActionListener(e -> confirmCheckout(cnicField.getText(), checkoutFrame));
        buttonPanel.add(confirmCheckoutButton);
        buttonPanel.add(Box.createHorizontalGlue());
        checkoutPanel.add(buttonPanel);

        mainPanel.add(checkoutPanel, gbc);

        // Center the white container vertically
        gbc.weighty = 1.0;
        mainPanel.add(Box.createVerticalGlue(), gbc);

        checkoutFrame.add(mainPanel);

        checkoutFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        checkoutFrame.setSize(800, 600); // Adjusted the size of the frame
        checkoutFrame.setLocationRelativeTo(null);
        checkoutFrame.setVisible(true);
    }

  
    private void confirmCheckout(String cnic, JFrame checkoutFrame) {
        try {
            // Assuming cnic is unique and identifies the resident
            String updateQuery = "UPDATE Residents SET checkout_status = 'T', room_number = 0 WHERE cnic = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, cnic);
                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    insertIntoCheckOutTable(cnic); // Call a method to insert into the check_out table
                    showMessage("Checkout confirmed for resident with CNIC: " + cnic);
                    checkoutFrame.dispose();
                } else {
                    showError("Failed to confirm checkout. Please check the CNIC and try again.");
                }
            }
        } catch (SQLException ex) {
            handleOperationError(ex, "Failed to confirm checkout. " + ex.getMessage());
        }
    }
    
    private void insertIntoCheckOutTable(String cnic) throws SQLException {
        // Assuming 'check_out_date' is a DATE column in the check_out table
        String insertQuery = "INSERT INTO check_out (resident_id, check_out_date) " +
                             "SELECT resident_id, SYSDATE FROM Residents WHERE cnic = ?";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setString(1, cnic);
            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted <= 0) {
                showError("Failed to insert into check_out table.");
            }
        }
    }

    ///////////////////////////
    //AVAILABLE ROOMS BUTTON
    ///////////////////////////
    
    private void showAvailableRooms() {
    	   try {
    	       String query = "WITH AllRooms AS (" +
    	               " SELECT level AS room_number" +
    	               " FROM dual" +
    	               " CONNECT BY level BETWEEN 1 AND 30" +
    	               ")," +
    	               "RoomsWithMaxCount AS (" +
    	               " SELECT" +
    	               "   r.room_number," +
    	               "   COUNT(r.resident_id) AS occupied_count," +
    	               "   CASE" +
    	               "       WHEN r.room_number BETWEEN 1 AND 10 THEN 2" +
    	               "       WHEN r.room_number BETWEEN 11 AND 30 THEN 4" +
    	               "       ELSE NULL" +
    	               "   END AS max_residents," +
    	               "   CASE" +
    	               "       WHEN r.room_number BETWEEN 1 AND 10 THEN 40000" +
    	               "       WHEN r.room_number BETWEEN 11 AND 30 THEN 20000" +
    	               "       ELSE NULL" +
    	               "   END AS rent" +
    	               " FROM Residents r" +
    	               " GROUP BY r.room_number" +
    	               ")" +
    	               "SELECT" +
    	               " r.room_number," +
    	               " CASE" +
    	               "   WHEN r.room_number BETWEEN 1 AND 10 THEN 2" +
    	               "   WHEN r.room_number BETWEEN 11 AND 30 THEN 4" +
    	               "   ELSE NULL" +
    	               " END AS max_residents," +
    	               " CASE" +
    	               "   WHEN o.occupied_count IS NULL THEN" +
    	               "       CASE" +
    	               "           WHEN r.room_number BETWEEN 1 AND 10 THEN 40000" +
    	               "           WHEN r.room_number BETWEEN 11 AND 30 THEN 20000" +
    	               "           ELSE NULL" +
    	               "       END" +
    	               "   ELSE" +
    	               "       CASE" +
    	               "           WHEN r.room_number BETWEEN 1 AND 10 THEN 40000" +
    	               "           WHEN r.room_number BETWEEN 11 AND 30 THEN 20000" +
    	               "           ELSE NULL" +
    	               "       END" +
    	               " END AS rent," +
    	               " CASE" +
    	               "   WHEN o.occupied_count IS NULL THEN" +
    	               "       CASE" +
    	               "           WHEN r.room_number BETWEEN 1 AND 10 THEN 2" +
    	               "           WHEN r.room_number BETWEEN 11 AND 30 THEN 4" +
    	               "           ELSE NULL" +
    	               "       END" +
    	               "   ELSE" +
    	               "       CASE" +
    	               "           WHEN r.room_number BETWEEN 1 AND 10 THEN 2 - o.occupied_count" +
    	               "           WHEN r.room_number BETWEEN 11 AND 30 THEN 4 - o.occupied_count" +
    	               "           ELSE NULL" +
    	               "       END" +
    	               " END AS residents_left" +
    	               " FROM AllRooms r" +
    	               " LEFT OUTER JOIN RoomsWithMaxCount o ON r.room_number = o.room_number" +
    	               " WHERE o.occupied_count IS NULL OR o.occupied_count < CASE" +
    	               "   WHEN r.room_number BETWEEN 1 AND 10 THEN 2" +
    	               "   WHEN r.room_number BETWEEN 11 AND 30 THEN 4" +
    	               "   ELSE NULL" +
    	               " END" +
    	               " ORDER BY r.room_number";

    	       try (PreparedStatement preparedStatement = connection.prepareStatement(query);
    	            ResultSet resultSet = preparedStatement.executeQuery()) {
    	           displayAvailableRooms(resultSet);
    	       }
    	   } catch (SQLException ex) {
    	       handleOperationError(ex, "Failed to fetch available rooms. " + ex.getMessage());
    	   }
    	}

    
    private void displayAvailableRooms(ResultSet resultSet) throws SQLException {
        JFrame availableRoomsFrame = new JFrame("Available Rooms");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align panels to the left
        Border emptyBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        
        // Create separate panels for each category of rooms
        JPanel partiallyFilledRoomsPanel = createRoomPanel("Partially Filled Rooms", emptyBorder);
        JPanel twoPeopleRoomsPanel = createRoomPanel("2 People Rooms", emptyBorder);
        JPanel fourPeopleRoomsPanel = createRoomPanel("4 People Rooms", emptyBorder);


        // Create tables for each category of rooms
        DefaultTableModel partiallyFilledRoomsTableModel = createTableModel();
        DefaultTableModel twoPeopleRoomsTableModel = createTableModel();
        DefaultTableModel fourPeopleRoomsTableModel = createTableModel();

        JTable partiallyFilledRoomsTable = createRoomTable(partiallyFilledRoomsTableModel);
        JTable twoPeopleRoomsTable = createRoomTable(twoPeopleRoomsTableModel);
        JTable fourPeopleRoomsTable = createRoomTable(fourPeopleRoomsTableModel);

        JScrollPane partiallyFilledRoomsScrollPane = new JScrollPane(partiallyFilledRoomsTable);
        JScrollPane twoPeopleRoomsScrollPane = new JScrollPane(twoPeopleRoomsTable);
        JScrollPane fourPeopleRoomsScrollPane = new JScrollPane(fourPeopleRoomsTable);

        partiallyFilledRoomsPanel.add(partiallyFilledRoomsScrollPane);
        twoPeopleRoomsPanel.add(twoPeopleRoomsScrollPane);
        fourPeopleRoomsPanel.add(fourPeopleRoomsScrollPane);

        while (resultSet.next()) {
            int roomNumber = resultSet.getInt("room_number");
            int maxResidents = resultSet.getInt("max_residents");
            int residentsLeft = resultSet.getInt("residents_left");
            int rent = resultSet.getInt("rent");

            Object[] row = {roomNumber, maxResidents, residentsLeft, rent};

            if (residentsLeft > 0 && residentsLeft < maxResidents) {
                partiallyFilledRoomsTableModel.addRow(row);
            } else if (residentsLeft == maxResidents) {
                if (maxResidents == 2) {
                    twoPeopleRoomsTableModel.addRow(row);
                } else if (maxResidents == 4) {
                    fourPeopleRoomsTableModel.addRow(row);
                }
            }
        }

        // Set preferred size to make tables take up only the required space
        partiallyFilledRoomsTable.setPreferredScrollableViewportSize(partiallyFilledRoomsTable.getPreferredSize());
        twoPeopleRoomsTable.setPreferredScrollableViewportSize(twoPeopleRoomsTable.getPreferredSize());
        fourPeopleRoomsTable.setPreferredScrollableViewportSize(fourPeopleRoomsTable.getPreferredSize());

        // Adjust column widths
        adjustColumnWidths(partiallyFilledRoomsTable);
        adjustColumnWidths(twoPeopleRoomsTable);
        adjustColumnWidths(fourPeopleRoomsTable);

        // Center the tables within the panel
        partiallyFilledRoomsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        twoPeopleRoomsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fourPeopleRoomsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add the panels to the main panel
        mainPanel.add(partiallyFilledRoomsPanel);
        mainPanel.add(twoPeopleRoomsPanel);
        mainPanel.add(fourPeopleRoomsPanel);
        mainPanel.setBorder(emptyBorder);
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        availableRoomsFrame.add(scrollPane);

        availableRoomsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        availableRoomsFrame.setSize(1800, 850);
        availableRoomsFrame.setLocationRelativeTo(null);
        availableRoomsFrame.setVisible(true);
    }

    private void adjustColumnWidths(JTable table) {
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // Adjust the width of the first column
        columnModel.getColumn(1).setPreferredWidth(50);  // Adjust the width of the second column
        columnModel.getColumn(2).setPreferredWidth(50); // Adjust the width of the third column
        columnModel.getColumn(3).setPreferredWidth(50);  // Adjust the width of the fourth column
    }


    private JPanel createRoomPanel(String title, Border emptyBorder) {
    	   JPanel roomPanel = new JPanel();
    	   roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));
    	   roomPanel.setBorder(emptyBorder); // Set the border on the room panel

    	   JLabel roomLabel = new JLabel(title);
    	   roomLabel.setFont(new Font("Arial", Font.BOLD, 16));
    	   roomLabel.setForeground(Color.BLUE);

    	   roomPanel.add(roomLabel);

    	   return roomPanel;
    	}


    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(new Object[]{"Room No", "Max Capacity", "Capacity Left", "Rent"}, 0);
    }

    private JTable createRoomTable(DefaultTableModel model) {
        JTable roomTable = new JTable(model);
        roomTable.setFont(new Font("Arial", Font.PLAIN, 12));
        roomTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        return roomTable;
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReceptionistAccessWindow());
    }
}