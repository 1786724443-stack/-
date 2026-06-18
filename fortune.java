package test;

import javax.swing.SwingUtilities;

public class fortune {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FortuneAppContext context = new FortuneAppContext();
            context.run();
        });
    }
}
