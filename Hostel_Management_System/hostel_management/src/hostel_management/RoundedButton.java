package hostel_management;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
class RoundedButton extends JButton {

    private final int width;
    private final int height;

    public RoundedButton(String text, int width, int height) {
        super(text);
        this.width = width;
        this.height = height;
        setStyles();
    }

    private void setStyles() {
        setBackground(new Color(1, 38, 65));
        setForeground(new Color(229, 241, 252));
        setFont(new Font("Times New Roman", Font.BOLD, 18));
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setBorderPainted(false);
        setFocusPainted(false);
        setPreferredSize(new Dimension(width, height)); // Set preferred size
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background with rounded corners using specified width and height
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, width - 1, height - 1, 20, 20);

        // Draw text
        g2d.setColor(getForeground());
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (width - fm.stringWidth(getText())) / 2;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(getText(), textX, textY);

        g2d.dispose();
    }
}