package pertemuan2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class pertemuan2 extends JPanel {

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(new Color(12, 16, 116));
        g2d.fillRect(10, 15, 90, 60);

        g2d.setColor(new Color(42, 19, 31));
        g2d.fillRect(130, 15, 90, 60);

        g2d.setColor(new Color(70, 7, 23));
        g2d.fillRect(250, 15, 90, 60);

        g2d.setColor(new Color(10, 10, 84));
        g2d.fillRect(10, 105, 90, 60);

        g2d.setColor(new Color(22, 21, 61));
        g2d.fillRect(130, 105, 90, 60);

        g2d.setColor(new Color(21, 98, 69));
        g2d.fillRect(250, 105, 90, 60);

        g2d.setColor(new Color(217, 146, 54));
        g2d.fillRect(10, 195, 90, 60);

        g2d.setColor(new Color(63, 121, 186));
        g2d.fillRect(130, 195, 90, 60);

        g2d.setColor(new Color(131, 121, 11));
        g2d.fillRect(250, 195, 90, 60);

    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Colors");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new pertemuan2());
        frame.setSize(360, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}