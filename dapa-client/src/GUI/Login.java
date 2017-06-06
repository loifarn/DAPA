package GUI;

import Handlers.ClientHandler;
import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    //Private variables and objects
    private JPanel pwPanel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel registerLabel;

    //Public variables and objects
    public JFrame loginFrame;
    public JTextField usernameField;
    public JPasswordField passwordField;
    public JButton loginButton;
    public JButton registerButton;

    public Login(ClientHandler h) {
        draw_Gui_Login(h);
    }

    private void draw_Gui_Login(ClientHandler h) {
        //Window configuration
        loginFrame = new JFrame("DAPA Chat Client");
        loginFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("Resources/chat.png")));
        loginFrame.setResizable(false);
        loginFrame.add(pwPanel);
        loginFrame.setSize(225,200);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //Adding listeners
        usernameField.addActionListener(h);
        usernameField.addMouseListener(h);
        passwordField.addActionListener(h);
        passwordField.addMouseListener(h);
        loginButton.addActionListener(h);
        registerButton.addActionListener(h);
    }

    //Getters and setters
    public JTextField getUsernameField() {
        return usernameField;
    }
    public void setUsernameField(JTextField usernameField) {
        this.usernameField = usernameField;
    }
    public JPasswordField getPasswordField() {
        return passwordField;
    }
    public void setPasswordField(JPasswordField passwordField) {
        this.passwordField = passwordField;
    }
}
