package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Un bouton Custom qui peut être arrondi (emprunt)
 */
public class RoundedButton extends JButton {
    private final int cornerRadius;

    public RoundedButton(String text, int cornerRadius) {
        super(text);
        this.cornerRadius = cornerRadius;
        setContentAreaFilled(false);
        setOpaque(false);
        setBackground(new Color(85,	60,	42));
        setForeground(Color.LIGHT_GRAY);
        setBorderPainted(false);
        setFocusPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isArmed()) {
            g2d.setColor(getBackground().darker());
        } else {
            g2d.setColor(getBackground());
        }

        g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));

        super.paintComponent(g2d);
        g2d.dispose();
    }


}
