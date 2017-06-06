package GUI;

import Handlers.ClientHandler;
import javax.swing.*;
import java.awt.*;

public class Client extends JFrame{
    //Private variables and objects
    private JPanel guiPanel;
    private JLabel channelLabel;
    private JScrollPane GDScrollPane;
    private JScrollPane OUScrollPane;

    //Public variables and objects
    public JFrame guiFrame;
    public JTextPane guiDisplay;
    public JFormattedTextField guiMessage;
    public JList channelList;
    public DefaultListModel listModel;
    public JTextPane displayUsers;
    public JTabbedPane paneTabs;
    public JButton channelManagerButton;

    public Client(ClientHandler h) {
        drawGUI(h);
    }

    private void drawGUI(ClientHandler h) {
        //List configuration
        listModel = new DefaultListModel();
        channelList.setModel(listModel);

        //Windows configuration
        guiFrame = new JFrame("DAPA Chat Client");
        guiFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("Resources/chat.png")));
        guiFrame.setResizable(true);
        guiFrame.add(guiPanel);
        guiFrame.setSize(850,525);
        guiFrame.setLocationRelativeTo(null);
        guiFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Adding listeners
        guiMessage.addKeyListener(h);
        channelList.addMouseListener(h);
        guiFrame.addWindowListener(h);
        channelList.addListSelectionListener(h);
        paneTabs.addChangeListener(h);
        channelManagerButton.addActionListener(h);
    }
}
