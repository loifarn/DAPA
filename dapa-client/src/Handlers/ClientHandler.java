package Handlers;

import GUI.Client;
import GUI.Login;
import GUI.Registration;
import Logic.ClientLogic;
import Logic.ClientCache;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements ActionListener, MouseListener, KeyListener, WindowListener, ListSelectionListener, ChangeListener {
    //Self initialization and program start.
    public static void main(String[] args) {
        System.out.println("Program started");
        new ClientHandler();
    }

    //Class declarations
    private ClientLogic logic;
    private Login loginScreen;
    private Registration registrationScreen;
    private Client gui;
    private WebHandler WebHandler;

    //Constructor that initializes classes and sets theme
    private ClientHandler() {
        logic = new ClientLogic();
        loginScreen = new Login(this);
        registrationScreen = new Registration(this);
        gui = new Client(this);

        logic.setLookAndFeel(loginScreen.loginFrame, true);
        logic.setLookAndFeel(registrationScreen.registrationFrame, false);
        logic.setLookAndFeel(gui.guiFrame, false);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // --------------------------------------- //
        // Actions performed by GUI screen
        // --------------------------------------- //

        if (e.getSource().equals(gui.channelManagerButton)) {
            Object[] option = {"Join", "Create", "Leave"};
            Object result = JOptionPane.showInputDialog(null,
                    "Choose Action",
                    "ChannelTask Manager",
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    option,
                    option[0]
            );

            if (result == "Join") {
                String answer = JOptionPane.showInputDialog("Channel to join");
                logic.joinChannel(WebHandler, answer);

            } else if(result == "Create") {
                String answer = JOptionPane.showInputDialog("Channel to create");
                logic.createChannel(WebHandler, answer);

            } else if(result == "Leave") {
                String answer = JOptionPane.showInputDialog("Channel to leave");
                logic.partChannel(WebHandler, gui, answer);
            }
        }

        // --------------------------------------- //
        // Actions performed by Login screen
        // --------------------------------------- //
        if (e.getSource().equals(loginScreen.loginButton)) {
            //Establishing connection with the server and attempting to verify credentials
            WebHandler = new WebHandler();
            logic.login(WebHandler, loginScreen);

            //Timeout to facilitate potential delay in server response.
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            if (WebHandler.validLogin) {
                //Loading logs and changing view to gui.
                logic.changeView(gui,loginScreen,registrationScreen);
                logic.initializeDefaultChannel(WebHandler, gui);
                logic.refreshUsers(gui);
                logic.refreshMessages(gui);
                ClientCache.getInstance().cachedGUI = gui;
                ClientCache.getInstance().panes.add(gui.guiDisplay);
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Error, can't reach server. Please try again later.");
            }
        }
        if (e.getSource().equals(loginScreen.registerButton)) {
            registrationScreen.registrationFrame.setVisible(true);
            loginScreen.loginFrame.setVisible(false);
        }


        // --------------------------------------- //
        // Actions performed by registration screen
        // --------------------------------------- //
        if (e.getSource().equals(registrationScreen.createButton)) {
            //Registering new user
            WebHandler = new WebHandler();
            logic.register(WebHandler, registrationScreen);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            if (WebHandler.validRegistration) {
                //Loading logs and changing view to gui.
                logic.changeView(gui,loginScreen,registrationScreen);
                logic.initializeDefaultChannel(WebHandler, gui);
                logic.refreshUsers(gui);
                logic.refreshMessages(gui);
                ClientCache.getInstance().cachedGUI = gui;
                ClientCache.getInstance().panes.add(gui.guiDisplay);

            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Error, can't reach server. Please try again later.");
            }
        }
        if (e.getSource().equals(registrationScreen.cancelButton)) {
            registrationScreen.registrationFrame.dispose();
            loginScreen.loginFrame.setVisible(true);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        // --------------------------------------- //
        // Mouse click events on GUI screen
        // --------------------------------------- //


        // --------------------------------------- //
        // Mouse click events on Login screen
        // --------------------------------------- //

        if (e.getSource().equals(loginScreen.usernameField)) {
            loginScreen.usernameField.setText("");
            if (loginScreen.passwordField.getPassword().length == 0) {
                loginScreen.passwordField.setText("password");
            }
        }

        if (e.getSource().equals(loginScreen.passwordField)) {
            loginScreen.passwordField.setText("");
            if (loginScreen.usernameField.getText().equals("")) {
                loginScreen.usernameField.setText("User Name");
            }
        }


        // --------------------------------------- //
        // Mouse click events on registration screen
        // --------------------------------------- //

        if (e.getSource().equals(registrationScreen.nameField)) {
            registrationScreen.nameField.setText("");
            if (registrationScreen.usernameField.getText().equals("")){
                registrationScreen.usernameField.setText("User Name");
            }

            if (registrationScreen.passwordfield.getPassword().length == 0) {
                registrationScreen.passwordfield.setText("password");
            }
        }
        if (e.getSource().equals(registrationScreen.usernameField)){
            registrationScreen.usernameField.setText("");
            if (registrationScreen.nameField.getText().equals("")) {
                registrationScreen.nameField.setText("Name");
            }
            if (registrationScreen.passwordfield.getPassword().length == 0) {
                registrationScreen.passwordfield.setText("password");
            }
        }
        if (e.getSource().equals(registrationScreen.passwordfield)) {
            registrationScreen.passwordfield.setText("");
            if (registrationScreen.nameField.getText().equals("")) {
                registrationScreen.nameField.setText("Name");
            }
            if (registrationScreen.usernameField.getText().equals("")){
                registrationScreen.usernameField.setText("User Name");
            }
        }


    }


    // --------------------------------------- //
    // Miscellaneous events
    // --------------------------------------- //

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //Checks if message is a private message, if it is: Formats message and sends to user.
        if (e.getSource().equals(gui.guiMessage) && e.getKeyCode() == KeyEvent.VK_ENTER ) {
            if (gui.guiMessage.getText().startsWith("/t")){
                if ((gui.guiMessage.getText().length()-2) != 0) {
                    System.out.println((gui.guiMessage.getText().length()-2));
                    String reg = " ";
                    String[] user = gui.guiMessage.getText().split(reg);
                    if (user.length >=1) {
                        logic.messagePrivateSend(WebHandler, gui, user[1]);
                    }
                }
            }
            else {
                logic.messageChannelSend(WebHandler, gui);
            }
            gui.guiMessage.setText("");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (e.getSource().equals(gui.guiFrame)) {
            logic.logWrite();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        //Selected tab = selected channel
        if (e.getSource().equals(gui.channelList)) {
            gui.paneTabs.setSelectedIndex(gui.channelList.getSelectedIndex());
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        //Selected channel = selected tab
        if (e.getSource().equals(gui.paneTabs)) {
            gui.channelList.setSelectedIndex(gui.paneTabs.getSelectedIndex());
        }
    }
}
