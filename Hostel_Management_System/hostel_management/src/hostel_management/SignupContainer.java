package hostel_management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SignupContainer extends JPanel {
    private DatabaseConnection dbConnection;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton signUpButton;
    private JCheckBox showPasswordCheckbox;

    public SignupContainer(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        // Set the background color of SignupContainer to white
        Color customColor = new Color(0xE5F1FC);
        setBackground(customColor);

        // Add SignupContainer to GridBagLayout with a fixed size and border
        JPanel innerPanel = createInnerPanel();
        innerPanel.setPreferredSize(new Dimension(400, 400)); // Set fixed width and height
        innerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around the border

        // Center the inner panel in the main panel
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(innerPanel, constraints);
    }

    private JPanel createInnerPanel() {
        // Create a rounded corner panel with white background
        JPanel innerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Dimension arcs = new Dimension(15, 15); // Border arc size
                int width = getWidth();
                int height = getHeight();
                Graphics2D graphics = (Graphics2D) g;
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw white rounded-rectangle
                Color customColor = new Color(0xE5F1FC);
                graphics.setColor(customColor);
                graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
                graphics.setColor(new Color(96, 179, 209)); // #60B3D1
                graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
            }
        };

        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

        // Center the innerPanel horizontally and vertically within the white container
        innerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        innerPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Add space between email label and "HOSTEL"
        innerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add "HOSTEL" text centered
        JLabel hostelLabel = new JLabel("COZY HAVEN ");
        hostelLabel.setFont(new Font("Times New Roman", Font.BOLD, 35));
        hostelLabel.setForeground(new Color(1, 38, 65));
        hostelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hostelLabel.setVerticalAlignment(SwingConstants.CENTER);
        innerPanel.add(hostelLabel);

        // Add space before and after email label
        innerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        emailLabel.setForeground(new Color(1, 38, 65));
        innerPanel.add(emailLabel);

        emailField = new JTextField(20); // Increase the number of columns
        emailField.setMaximumSize(new Dimension(800, 60));
        innerPanel.add(emailField);

        // Add space before and after password label
        innerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        passwordLabel.setForeground(new Color(1, 38, 65));
        innerPanel.add(passwordLabel);

        passwordField = new JPasswordField(100); // Increase the number of columns
        passwordField.setMaximumSize(new Dimension(800, 60));
        innerPanel.add(passwordField);

        // Add checkbox for showing password
        showPasswordCheckbox = new JCheckBox("Show Password");
        showPasswordCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                togglePasswordVisibility(showPasswordCheckbox.isSelected());
            }
        });
        innerPanel.add(showPasswordCheckbox);

        // Add space before and after the button
        innerPanel.add(Box.createVerticalGlue());

        // Use the styled button class
        signUpButton = new RoundedButton("Sign Up", 94, 45);

        hostelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        innerPanel.add(signUpButton);
        
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if the email and password are correct (add your authentication logic)
                String email = emailField.getText();
                String password = String.valueOf(passwordField.getPassword());

                if (dbConnection.authenticateUser(email, password)) {
                    String designation = dbConnection.getDesignation(email);
                    System.out.println("Designation: " + designation);

                    // Open a new window based on the user's designation
                    openAccessWindow(designation);
                } else {

                    // Display an error message or handle incorrect credentials
                    JOptionPane.showMessageDialog(null, "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
       
        return innerPanel;
    }

    private void openAccessWindow(String designation) {
        // Create a new window based on the user's designation
        JFrame accessWindow = null;

        switch (designation.toLowerCase()) {
            case "admin":
                accessWindow = new AdminAccessWindow();
                break;
            case "receptionist":
                accessWindow = new ReceptionistAccessWindow();
                break;
            case "manager":
                accessWindow = new ManagerAccessWindow();
                break;
            case "accountant":
                accessWindow = new AccountantAccessWindow();
                break;
                default:
                // Handle unknown designation
                break;
        }

        if (accessWindow != null) {
            accessWindow.setSize(1800, 850);
            accessWindow.setLocationRelativeTo(null);
            accessWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            accessWindow.setVisible(true);
        }
        
    }

        

    public void togglePasswordVisibility(boolean visible) {
        passwordField.setEchoChar(visible ? 0 : '*');
    }
}