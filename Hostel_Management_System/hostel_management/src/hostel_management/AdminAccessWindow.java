package hostel_management;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AdminAccessWindow extends JFrame {
	private String imagePath;
	int result;
	 private JFileChooser fileChooser;
	 private DefaultListModel<String> employeeList;
	 private JPanel cardPanel;
	 private CardLayout cardLayout;
	 private JFrame viewProfilesFrame;
	 
    public AdminAccessWindow() {
        setTitle("Admin Window");

        JPanel mainPanel = new BackgroundPanel(new GridBagLayout(), "./admin_background.jpg.jpeg");
     

        JButton addEmployeeButton = createStyledButton("Add Employee");
        JButton viewProfilesButton = createStyledButton("View Profiles");

        // Set icons for the buttons with a specific size
        addEmployeeButton.setIcon(resizeImageIcon(new ImageIcon("./addEmployee.png"), 280, 280));
        viewProfilesButton.setIcon(resizeImageIcon(new ImageIcon("./viewProfile.png"), 280, 280));


        
        addEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddEmployeeForm();
            }
        });

        viewProfilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	openViewProfilesWindow();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.ipadx = 5; // horizontal padding
        gbc.ipady = 5; // vertical padding

        mainPanel.add(addEmployeeButton, gbc);
        gbc.gridx = 1;
        mainPanel.add(viewProfilesButton, gbc);
       
       
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
    
    /////////////////////////////////////////
    // ADD EMPLOYEE AND SUPPOTING METHODS
    /////////////////////////////////////////
    
    
    private void openAddEmployeeForm() {
    	JFileChooser fileChooser = new JFileChooser();

        JFrame addEmployeeFrame = new JFrame("Add Employee Form");
        
        JPanel addEmployeePanel = new BackgroundPanel(new GridLayout(0, 2), "./Admin2.jpg");
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add form components
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

        JTextField dobField = new JTextField();
        dobField.setPreferredSize(new Dimension(200, 25));
        
        JTextField cnicField = new JTextField();
        cnicField.setPreferredSize(new Dimension(200, 25));
        
        JTextField contactNoField = new JTextField();
        contactNoField.setPreferredSize(new Dimension(200, 25));
        
        JTextField fatherNameField = new JTextField();
        fatherNameField.setPreferredSize(new Dimension(200, 25));

        JTextField fatherCnicField = new JTextField();
        fatherCnicField.setPreferredSize(new Dimension(200, 25));

        JTextField permanentAddressField = new JTextField();
        permanentAddressField.setPreferredSize(new Dimension(200, 25));

        JTextField postalAddressField = new JTextField();
        postalAddressField.setPreferredSize(new Dimension(200, 25));

        JTextField designationField = new JTextField();
        designationField.setPreferredSize(new Dimension(200, 25));

        JTextField ageField = new JTextField();
        ageField.setEditable(false);
        ageField.setPreferredSize(new Dimension(200, 25));
        addComponent(formPanel, ageField, gbc, 1, 11);

        JLabel selectedImageLabel = new JLabel("No image selected");
        RoundedButton uploadImageButton = new RoundedButton("Upload", 110, 26);
        uploadImageButton.setPreferredSize(new Dimension(100, 25));


        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle image upload
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(filter);

                int result = fileChooser.showOpenDialog(AdminAccessWindow.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imagePath = selectedFile.getAbsolutePath(); // Update imagePath with the correct path
                    selectedImageLabel.setText("Selected Image: " + selectedFile.getName());
                    // Save the file path or process the file as needed
                }

                // Do not hide or dispose of the addEmployeeFrame here
            }
        });

        RoundedButton submitButton = new RoundedButton("Submit", 110, 26);
        submitButton.setPreferredSize(new Dimension(100, 25));
        
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get values from the form fields
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String gender = getSelectedGender(maleRadioButton, femaleRadioButton);
                String dob = dobField.getText();
                String cnic = cnicField.getText();
                String contactNo = contactNoField.getText();
                String fatherName = fatherNameField.getText();
                String fatherCnic = fatherCnicField.getText();
                String permanentAddress = permanentAddressField.getText();
                String postalAddress = postalAddressField.getText();
                String designation = designationField.getText();

                // Calculate age from date of birth
                int age = calculateAge(dob);
                ageField.setText(Integer.toString(age));

                // Validate the data (you can add more validation as needed)

                // Process the data, generate email, password, etc.
                String employeeId = generateEmployeeId();
                String email = generateEmail(firstName, lastName, employeeId);
                String password = generatePassword(firstName);

                // Get the selected image file path
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imagePath = selectedFile.getAbsolutePath(); // Update imagePath with the correct path
                    selectedImageLabel.setText("Selected Image: " + selectedFile.getName());
                }

                // Insert data into the employeesP table
                if (insertEmployeeData(firstName, lastName, dob, cnic, fatherName, fatherCnic, contactNo,
                        permanentAddress, postalAddress, designation, gender)) {
                    // Display success message
                    JOptionPane.showMessageDialog(addEmployeeFrame,
                            "Employee data added successfully.\nEmail: " + email + "\nPassword: " + password);
                } else {
                    // Display an error message if insertion fails
                    JOptionPane.showMessageDialog(addEmployeeFrame, "Failed to add employee data.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                // Close the addEmployeeFrame
                addEmployeeFrame.dispose();
            }
        });

        //Adding all the components to the form
        addComponent(formPanel, new JLabel("First Name:"), gbc, 0, 0);
        addComponent(formPanel, firstNameField, gbc, 1, 0);

        addComponent(formPanel, new JLabel("Last Name:"), gbc, 0, 1);
        addComponent(formPanel, lastNameField, gbc, 1, 1);

        addComponent(formPanel, new JLabel("Gender:"), gbc, 0, 2);
        addComponent(formPanel, genderPanel, gbc, 1, 2);

        addComponent(formPanel, new JLabel("Date of Birth (YYYY-MM-DD):"), gbc, 0, 3);
        addComponent(formPanel, dobField, gbc, 1, 3);

        addComponent(formPanel, new JLabel("CNIC (Format: 12345-1234567-1):"), gbc, 0, 4);
        addComponent(formPanel, cnicField, gbc, 1, 4);

        addComponent(formPanel, new JLabel("Contact No (Format: 1234567890123):"), gbc, 0, 5);
        addComponent(formPanel, contactNoField, gbc, 1, 5);

        addComponent(formPanel, new JLabel("Father's Name:"), gbc, 0, 6);
        addComponent(formPanel, fatherNameField, gbc, 1, 6);

        addComponent(formPanel, new JLabel("Father's CNIC (Format: 12345-1234567-1):"), gbc, 0, 7);
        addComponent(formPanel, fatherCnicField, gbc, 1, 7);

        addComponent(formPanel, new JLabel("Permanent Address:"), gbc, 0, 8);
        addComponent(formPanel, permanentAddressField, gbc, 1, 8);

        addComponent(formPanel, new JLabel("Postal Address:"), gbc, 0, 9);
        addComponent(formPanel, postalAddressField, gbc, 1, 9);

        addComponent(formPanel, new JLabel("Designation:"), gbc, 0, 10);
        addComponent(formPanel, designationField, gbc, 1, 10);

        addComponent(formPanel, new JLabel("Age:"), gbc, 0, 11);
        addComponent(formPanel, ageField, gbc, 1, 11);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        addComponent(formPanel, new JLabel(""), gbc, 0, 12);
        
        addComponent(formPanel, selectedImageLabel, gbc, 0, 13);
        addComponent(formPanel, uploadImageButton, gbc, 1, 13);
        
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        addComponent(formPanel, new JLabel(""), gbc, 0, 14);
        
        gbc.gridwidth = GridBagConstraints.RELATIVE;  // Make the Submit button span the remaining columns
        addComponent(formPanel, submitButton, gbc, 0, 15);
        
        
        formPanel.add(submitButton);

        addEmployeePanel.add(formPanel); // Add formPanel to addEmployeePanel

        addEmployeeFrame.add(addEmployeePanel);
        addEmployeeFrame.setSize(1800, 850);
        addEmployeeFrame.setLocationRelativeTo(null);
        addEmployeeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only the addEmployeeFrame
        addEmployeeFrame.setVisible(true);
    }
    
    
 // FUNCTIONS RELATED TO ADDING EMPLOYEE
    
    private void addComponent(Container container, Component component, GridBagConstraints gbc, int gridx, int gridy) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        container.add(component, gbc);
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

    private boolean insertEmployeeData(String firstName, String lastName, String dob, String cnic, String fatherName,
            String fatherCnic, String contactNo, String permanentAddress,
            String postalAddress, String designation, String gender) {
    	try (Connection connection = new DatabaseConnection().getConnection();
    	         PreparedStatement preparedStatement = connection.prepareStatement(
    	                 "INSERT INTO employeesP (firstName, lastName, dateOfBirth, age, cnic, fatherName, " +
    	                         "fatherCnic, contactNo, permanentAddress, postalAddress, designation, gender, employeeImage) " +
    	                         "VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
    		
    			      // Calculate age from date of birth
    			      int age = calculateAge(dob);

    			      // Set values for the prepared statement
    			      preparedStatement.setString(1, firstName);
    			      preparedStatement.setString(2, lastName);
    			      preparedStatement.setString(3, dob);
    			      preparedStatement.setInt(4, age);
    			      preparedStatement.setString(5, cnic);
    			      preparedStatement.setString(6, fatherName);
    			      preparedStatement.setString(7, fatherCnic);
    			      preparedStatement.setString(8, contactNo);
    			      preparedStatement.setString(9, permanentAddress);
    			      preparedStatement.setString(10, postalAddress);
    			      preparedStatement.setString(11, designation);
    			      preparedStatement.setString(12, gender);
    			      File imageFile = new File(imagePath);
    			        if (imageFile.exists()) {
    			            preparedStatement.setBinaryStream(13, new FileInputStream(imageFile));
    			        } else {
    			            System.out.println("The file does not exist.");
    			        }


    			      // Execute the update
    			      int rowsAffected = preparedStatement.executeUpdate();
    			      // Return true if at least one row was affected (insert successful)
    			      return rowsAffected > 0;
    			} catch (SQLException | IOException ex) {
    			  ex.printStackTrace(); // Log or handle the exception appropriately
    			  return false;
    			}
}

    private int calculateAge(String dob) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int birthYear = Integer.parseInt(dob.substring(0, 4));
        return currentYear - birthYear;
    }



    private String generateEmployeeId() {
        return "EMP123";
    }

    private String generateEmail(String firstName,String lastName, String employeeId) {
        return firstName.toLowerCase() + "." + lastName.toLowerCase() + employeeId + "@hostel.com";
    }

    private String generatePassword(String firstName) {
        return firstName + "@123";
    }


    // VIEW PROFILE BUTTON 
    
    private void openViewProfilesWindow() {
    	viewProfilesFrame = new JFrame("View Profiles");

        viewProfilesFrame.setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField searchField = new JTextField(10);
        JButton searchButton = new JButton("Search");
        searchButton.setFocusPainted(false);

        JComboBox<String> filterComboBox = new JComboBox<>(new String[]{"Receptionist", "Admin", "Accountant", "Manager"});
        JButton filterButton = new JButton("Filter");

        // Add an EmptyBorder to create space at the top of the panel
        searchPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        searchPanel.add(new JLabel("Employee ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        searchPanel.add(new JLabel("Filter by Designation:"));
        searchPanel.add(filterComboBox);
        searchPanel.add(filterButton);

        searchPanel.setPreferredSize(new Dimension(searchPanel.getPreferredSize().width, 80));

        viewProfilesFrame.add(searchPanel, BorderLayout.NORTH);
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        searchPanel.setBackground(new Color(96, 179, 209));
        cardPanel.setBackground(Color.white);
        
        // Set the preferred size of cardPanel
        cardPanel.setPreferredSize(new Dimension(200, 100)); // Adjust the width and height as needed

        // Add cardPanel to the mainPanel
        viewProfilesFrame.add(cardPanel);

        DefaultListModel<String> employeeListModel = new DefaultListModel<>();
        JList<String> employeeJList = new JList<>(employeeListModel);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle search functionality
                String employeeId = searchField.getText();
                displayEmployeeData(employeeId, employeeListModel);
            }
        });

        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle filter functionality
                String designation = (String) filterComboBox.getSelectedItem();
                displayFilteredEmployees(designation, employeeListModel);
            }
        });


        viewProfilesFrame.setSize(1800, 850);
        viewProfilesFrame.setLocationRelativeTo(null);
        viewProfilesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        viewProfilesFrame.setVisible(true);
    }

    private void updateEmployeeList(DefaultListModel<String> employeeListModel, List<EmployeeData> employeeDataList) {
        // Clear the existing data in the employeeListModel
        employeeListModel.clear();

        // Iterate through employeeDataList and format each employee's data
        for (EmployeeData employeeData : employeeDataList) {
            String formattedData = formatEmployeeDataAsString(employeeData);
            // Add the formatted data to the employeeListModel
            employeeListModel.addElement(formattedData);
        }
    }
    
    private void displayEmployeeData(String employeeId, DefaultListModel<String> employeeListModel) {
        // After fetching data
        List<EmployeeData> employeeDataList = fetchDataFromDatabase(employeeId);

        // Update the JList with the fetched data
        updateEmployeeList(employeeListModel, employeeDataList);

        // Show the employee data in a card
        showEmployeeDataInCard(employeeDataList, true);
    }
    private void displayFilteredEmployees(String designation, DefaultListModel<String> employeeListModel) {
        // Clear the existing data in the employeeListModel
        employeeListModel.clear();

        // After fetching data based on designation
        List<EmployeeData> employeeDataList = fetchFilteredDataFromDatabase(designation);

        // Create a table model with column names and 0 rows
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"Name", "Father Name", "Contact No", "CNIC", "Age", "Contact No", "Designation", "Email"}, 0);

        // Iterate through employeeDataList
        for (EmployeeData employeeData : employeeDataList) {
            // Add a row to the table model for each employee
            tableModel.addRow(new Object[]{
                    employeeData.firstName + " " + employeeData.lastName,
                    employeeData.fatherName,
                    employeeData.contactNo,
                    employeeData.cnic,
                    employeeData.age,
                    employeeData.contactNo,
                    employeeData.designation,
                    fetchEmailFromDatabase(employeeData.firstName, employeeData.lastName)
            });
        }

        // Create a JTable with the table model
        JTable employeeTable = new JTable(tableModel);

        // Create a JScrollPane to hold the JTable
        JScrollPane scrollPane = new JScrollPane(employeeTable);

        // Remove existing components from the cardPanel
        cardPanel.removeAll();

        // Add the JScrollPane directly to the cardPanel
        cardPanel.add(scrollPane);

        // Update the UI
        cardPanel.revalidate();
        cardPanel.repaint();

        // Set the visibility of the frame to true
        viewProfilesFrame.setVisible(true);
    }

    private String fetchEmailFromDatabase(String firstName, String lastName) {
        // Implement logic to fetch employee email from the database based on first name and last name
        // Use a try-with-resources block to ensure proper resource closure

        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT email FROM employeesP WHERE firstName = ? AND lastName = ?")) {

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("email");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return "N/A"; // Return a default value if email is not found
    }
    
    private void showEmployeeDataInCard(List<EmployeeData> employeeDataList, boolean isSearch) {
        if (isSearch) {
            // Create a new JPanel for the employee data card
            JPanel employeeCardPanel = new JPanel();
            employeeCardPanel.setLayout(new BoxLayout(employeeCardPanel, BoxLayout.Y_AXIS));
            employeeCardPanel.setBackground(Color.white);

            // Set a small vertical gap between components
            int verticalGap = 5;
            employeeCardPanel.setBorder(BorderFactory.createEmptyBorder(verticalGap, 0, verticalGap, 0));

            // Iterate through employeeDataList
            for (EmployeeData employeeData : employeeDataList) {
                // Add a separator between employee entries
                JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                employeeCardPanel.add(separator);

                // Create a panel to hold the image with specified size
                JPanel imagePanel = new JPanel();
                imagePanel.setPreferredSize(new Dimension(50, 150)); // Set the image size to 100x100
                imagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

                // Show the employee image only if it's a search operation
                showEmployeeImage(employeeData.employeeImage, imagePanel);
                employeeCardPanel.add(imagePanel);

                // Add a small vertical gap after the image
                employeeCardPanel.add(Box.createRigidArea(new Dimension(0, verticalGap)));

                // Add data labels to the panel with centered alignment
                addCenteredDataLabel(employeeCardPanel, "Name: " + employeeData.firstName + " " + employeeData.lastName);
                addCenteredDataLabel(employeeCardPanel, "Father Name: " + employeeData.fatherName + "");
                addCenteredDataLabel(employeeCardPanel, "Designation: " + employeeData.designation + "");
                addCenteredDataLabel(employeeCardPanel, "Contact no: " + employeeData.contactNo + "");
                addCenteredDataLabel(employeeCardPanel, "CNIC: " + employeeData.cnic + "");
                addCenteredDataLabel(employeeCardPanel, "Age: " + employeeData.age);
                addCenteredDataLabel(employeeCardPanel, "Gender: " + employeeData.gender);
            }

            // Add some space between the data and the bottom of the window
            employeeCardPanel.add(Box.createVerticalGlue());

            // Add the card panel to the cardPanel with a unique name
            cardPanel.add(employeeCardPanel, "employeeCard");

            // Show the card with the employee data
            cardLayout.show(cardPanel, "employeeCard");

        } else {
            // Handle the case for filter by designation differently
            // Display the list of employees using a JList

            // Create a DefaultListModel to store the data for the JList
            DefaultListModel<String> employeeListModel = new DefaultListModel<>();

            // Iterate through employeeDataList
            for (EmployeeData employeeData : employeeDataList) {
                // Format each employee's data and add it to the model
                String formattedData = formatEmployeeDataAsString(employeeData);
                employeeListModel.addElement(formattedData);
            }

            // Create a JList with the DefaultListModel
            JList<String> employeeJList = new JList<>(employeeListModel);

            // Create a JScrollPane to hold the JList
            JScrollPane scrollPane = new JScrollPane(employeeJList);
            scrollPane.setBackground(Color.white);

            // Add the JScrollPane directly to the viewProfilesFrame
            viewProfilesFrame.add(scrollPane, BorderLayout.CENTER);
            viewProfilesFrame.setBackground(Color.white);

            // Set the visibility of the frame to true
            viewProfilesFrame.setVisible(true);
        }
    }


    private void addCenteredDataLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
    }

    
    private void showEmployeeDataOnCard(EmployeeData employeeData) {
        // Create a new JPanel for the employee data card
        JPanel employeeCardPanel = new JPanel();
        employeeCardPanel.setLayout(new BoxLayout(employeeCardPanel, BoxLayout.Y_AXIS));

        // Show the employee image at the top
        showEmployeeImage(employeeData.employeeImage, employeeCardPanel);

        // Add employee data to the card panel
        addDataLabel(employeeCardPanel, "Name: " + employeeData.firstName + " " + employeeData.lastName);
        addDataLabel(employeeCardPanel, "Father Name: " + employeeData.fatherName);
        addDataLabel(employeeCardPanel, "Contact No: " + employeeData.contactNo);
        addDataLabel(employeeCardPanel, "CNIC: " + employeeData.cnic);
        addDataLabel(employeeCardPanel, "Age: " + employeeData.age);
        addDataLabel(employeeCardPanel, "Gender: " + employeeData.gender);
        // Add more data labels as needed

        // Create a new card with a unique name
        String cardName = "employeeCard_" + employeeData.firstName + "_" + employeeData.lastName;
        cardPanel.add(employeeCardPanel, cardName);

        // Show the card with the employee data
        cardLayout.show(cardPanel, cardName);
    }
    
    private void addDataLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        panel.add(label);
    }
    
    private void showEmployeeImage(byte[] employeeImageBytes, JPanel panel) {
        if (employeeImageBytes != null && employeeImageBytes.length > 0) {
            // Create an ImageIcon from the byte array
            ImageIcon imageIcon = new ImageIcon(employeeImageBytes);

            // Create a JLabel to display the image
            JLabel imageLabel = new JLabel(imageIcon);

            // Set the background color of the panel
            panel.setBackground(Color.WHITE);

            // Add the image label to the panel
            panel.add(imageLabel);
        }
    }
    
    private List<EmployeeData> fetchDataFromDatabase(String employeeId) {
        // Implement logic to fetch employee data from the database based on the employee ID
        // Use a try-with-resources block to ensure proper resource closure
        List<EmployeeData> employeeDataList = new ArrayList<>();

        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM employeesP WHERE employeeID = ?")) {

            preparedStatement.setString(1, employeeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    EmployeeData employeeData = new EmployeeData(
                            resultSet.getString("firstName"),
                            resultSet.getString("lastName"),
                            resultSet.getString("gender"),
                            resultSet.getString("dateOfBirth"),
                            resultSet.getInt("age"),
                            resultSet.getString("cnic"),
                            resultSet.getString("contactNo"),
                            resultSet.getString("fatherName"),
                            resultSet.getString("fatherCnic"),
                            resultSet.getString("permanentAddress"),
                            resultSet.getString("postalAddress"),
                            resultSet.getString("designation"),
                            resultSet.getBytes("employeeImage")
                    );
                    employeeDataList.add(employeeData);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return employeeDataList;
    }

    private List<EmployeeData> fetchFilteredDataFromDatabase(String designation) {
        List<EmployeeData> filteredEmployeeList = new ArrayList<>();

        // Update the SQL query to filter employees by designation
        String sqlQuery = "SELECT * FROM employeesP WHERE LOWER(designation) = LOWER(?)";
        
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            preparedStatement.setString(1, designation);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    EmployeeData employeeData = new EmployeeData(
                            resultSet.getString("firstName"),
                            resultSet.getString("lastName"),
                            resultSet.getString("gender"),
                            resultSet.getString("dateOfBirth"),
                            resultSet.getInt("age"),
                            resultSet.getString("cnic"),
                            resultSet.getString("contactNo"),
                            resultSet.getString("fatherName"),
                            resultSet.getString("fatherCnic"),
                            resultSet.getString("permanentAddress"),
                            resultSet.getString("postalAddress"),
                            resultSet.getString("designation"),
                            resultSet.getBytes("employeeImage")
                    );
                    filteredEmployeeList.add(employeeData);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return filteredEmployeeList;
    }

 
    private String formatEmployeeDataAsString(EmployeeData employeeData) {
        // Format the employee data as a string
        return String.format("Name: %s %s, Gender: %s, DOB: %s, Age: %d, CNIC: %s",
                employeeData.firstName, employeeData.lastName, employeeData.gender,
                employeeData.dateOfBirth, employeeData.age, employeeData.cnic);
    }
    
    private static class EmployeeData {
        // Define a class to hold employee data
        // You can adjust this based on the actual structure of your data
        private final String firstName;
        private final String lastName;
        private final String gender;
        private final String dateOfBirth;
        private final int age;
        private final String cnic;
        private final String contactNo;
        private final String fatherName;
        private final String fatherCnic;
        private final String permanentAddress;
        private final String postalAddress;
        private final String designation;
        private final byte[] employeeImage;

        public EmployeeData(String firstName, String lastName, String gender, String dateOfBirth, int age,
                            String cnic, String contactNo, String fatherName, String fatherCnic,
                            String permanentAddress, String postalAddress, String designation,
                            byte[] employeeImage) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.gender = gender;
            this.dateOfBirth = dateOfBirth;
            this.age = age;
            this.cnic = cnic;
            this.contactNo = contactNo;
            this.fatherName = fatherName;
            this.fatherCnic = fatherCnic;
            this.permanentAddress = permanentAddress;
            this.postalAddress = postalAddress;
            this.designation = designation;
            this.employeeImage = employeeImage;
        }
    }
    public static void main(String[] args) {
        new AdminAccessWindow();
    }

}