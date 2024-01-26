package hostel_management;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SignupPanel extends JPanel {
    private Connection connection;
    private DatabaseConnection dbConnection;
    private JPanel whiteContainer;
    private JPanel blueContainer;

    public SignupPanel(Connection connection, DatabaseConnection dbConnection) {
        this.connection = connection;
        this.dbConnection = dbConnection;

        setLayout(new BorderLayout());

        // Create white and blue containers with image placeholder
        createContainersWithImagePlaceholder();

        // Create signup container centered in the white container
        SignupContainer signupContainer = new SignupContainer(dbConnection);
        whiteContainer.add(Box.createVerticalGlue());
        whiteContainer.add(signupContainer);
        whiteContainer.add(Box.createVerticalGlue());
    }

    protected void createContainersWithImagePlaceholder() {
        whiteContainer = new JPanel();
        whiteContainer.setLayout(new BoxLayout(whiteContainer, BoxLayout.Y_AXIS));
        whiteContainer.setBackground(Color.WHITE);
        add(whiteContainer, BorderLayout.CENTER);

        blueContainer = new JPanel();
        blueContainer.setLayout(new BoxLayout(blueContainer, BoxLayout.Y_AXIS));
        blueContainer.setBackground(new Color(96, 179, 209)); // #60B3D1

        // Set the preferred size for the blue container
        blueContainer.setPreferredSize(new Dimension(430, getHeight()));

        add(blueContainer, BorderLayout.EAST);

        // Add an image placeholder centered in the blue container
        JLabel imageLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon("./hostel-icon.png.jpeg"); // Set the actual path
        imageLabel.setIcon(imageIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        blueContainer.add(Box.createVerticalGlue());
        blueContainer.add(imageLabel);
        blueContainer.add(Box.createVerticalGlue());


        // Add SignupContainer to the white container
        SignupContainer signupContainer = new SignupContainer(dbConnection); // Pass dbConnection
        whiteContainer.add(signupContainer);


        // Add space before and after the button
        whiteContainer.add(Box.createVerticalGlue());

        // Add SignupContainer to the white container
        whiteContainer.add(signupContainer);
        whiteContainer.add(Box.createVerticalGlue());
    }

    public JPanel getWhiteContainer() {
        return whiteContainer;
    }

    public JPanel getBlueContainer() {
        return blueContainer;
    }
}