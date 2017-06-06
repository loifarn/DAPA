package GUI;

import Handlers.ClientHandler;
import javax.swing.*;
import java.awt.*;

public class Registration {
    //Private variables and objects
    private JPanel regPanel;
    private JLabel nameLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;

    //Public variables and objects
    public JFrame registrationFrame;
    public JPasswordField passwordfield;
    public JFormattedTextField usernameField;
    public JFormattedTextField nameField;
    public JButton createButton;
    public JButton cancelButton;

    public Registration(ClientHandler h) {
        drawRegWindow(h);
    }

    private void drawRegWindow(ClientHandler h) {
        //Window configuration
        registrationFrame = new JFrame("User Registration");
        registrationFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("Resources/chat.png")));
        registrationFrame.setResizable(false);
        registrationFrame.add(regPanel);
        registrationFrame.setSize(300,185);
        registrationFrame.setLocationRelativeTo(null);
        registrationFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //Adding listeners
         passwordfield.addMouseListener(h);
         usernameField.addMouseListener(h);
         nameField.addMouseListener(h);
         createButton.addActionListener(h);
         cancelButton.addActionListener(h);
    }
}
