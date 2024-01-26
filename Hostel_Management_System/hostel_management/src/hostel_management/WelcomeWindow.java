package hostel_management;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class WelcomeWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private int yPosition = 0;
    private JPanel curtainPanel;
    private Connection connection;
    private DatabaseConnection dbConnection;

    public WelcomeWindow(Connection connection, DatabaseConnection dbConnection) {
        this.connection = connection;
        this.dbConnection = dbConnection;

        getContentPane().setBackground(new Color(1, 38, 65));

        curtainPanel = new JPanel();
        curtainPanel.setLayout(new BoxLayout(curtainPanel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("WELCOME");
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);
        welcomeLabel.setFont(new Font("Lucida Handwriting", Font.BOLD, 80));
        welcomeLabel.setForeground(new Color(244, 251, 253));

        curtainPanel.add(Box.createVerticalGlue());
        curtainPanel.add(welcomeLabel);
        curtainPanel.add(Box.createVerticalGlue());
        curtainPanel.setBackground(new Color(1, 38, 65));

        add(curtainPanel);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    Timer timer = new Timer(5, e -> {
                        if (yPosition > -getHeight()) {
                        	curtainPanel.setLocation(curtainPanel.getX(), curtainPanel.getY() - 10);  // Move the curtain panel
                            yPosition -= 8;
                        } else {
                            ((Timer) e.getSource()).stop();
                            createHeaderAndSignupSection();
                        }
                    });
                    timer.start();
                }
            }
        });
    }

    private void createHeaderAndSignupSection() {
        JPanel mainContainer = new JPanel(new BorderLayout());
        add(mainContainer);

        SignupPanel signupPanel = new SignupPanel(connection, dbConnection);
        signupPanel.createContainersWithImagePlaceholder();

        mainContainer.add(signupPanel.getWhiteContainer(), BorderLayout.CENTER);
        mainContainer.add(signupPanel.getBlueContainer(), BorderLayout.EAST);

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseConnection dbConnection = new DatabaseConnection();
            Connection connection = dbConnection.getConnection();

            WelcomeWindow welcomeWindow = new WelcomeWindow(connection, dbConnection);
            welcomeWindow.setSize(1800, 850);
            welcomeWindow.setLocationRelativeTo(null);
            welcomeWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            welcomeWindow.setVisible(true);
        });
    }
}