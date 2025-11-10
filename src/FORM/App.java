// FORM/App.java
package FORM;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginUI().setVisible(true);  // CHỈ MỞ LOGINUI
        });
    }
}