package Logic;

import GUI.Client;
import GUI.Login;
import GUI.Registration;
import com.google.gson.Gson;
import com.vdurmont.emoji.EmojiParser;
import web.Requests.ChannelCreate;
import web.Requests.ChannelJoin;
import web.Requests.ChannelPart;
import Handlers.WebHandler;
import web.Objects.ObjChannel;
import web.Objects.ObjMessage;
import web.Objects.ObjSender;
import web.Requests.MessageChannel;
import web.Requests.MessagePrivate;
import web.Requests.UserLogin;
import web.Requests.UserRegister;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.io.*;
import java.util.*;
import java.util.Timer;

public class ClientLogic {

    public ClientLogic() {
    }

    //--------------------//
    // User Functionality //
    //--------------------//

    public void login(WebHandler WebHandler, Login loginScreen) {
        Gson gson = new Gson();
        UserLogin login = new UserLogin();
        login.setUsername(loginScreen.getUsernameField().getText());
        login.setPassword(new String(loginScreen.getPasswordField().getPassword()));
        try {
            WebHandler.currentSession.getBasicRemote().sendText(gson.toJson(login));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void register(WebHandler WebHandler, Registration regScreen) {
        Gson gson = new Gson();
        UserRegister register = new UserRegister();
        register.setDisplay_name(regScreen.nameField.getText());
        register.setUsername(regScreen.usernameField.getText());
        register.setPassword(new String(regScreen.passwordfield.getPassword()));
        try {
            WebHandler.currentSession.getBasicRemote().sendText(gson.toJson(register));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-------------------------//
    // Messaging Functionality //
    //-------------------------//

    public void messageChannelSend(WebHandler webHandler, Client gui) {
        Gson gson = new Gson();
        MessageChannel messageChannel = new MessageChannel();

        ObjMessage content = new ObjMessage();
        content.setContent(gui.guiMessage.getText());
        messageChannel.setMessage(content);

        ObjChannel name = new ObjChannel();
        name.setName(gui.channelList.getSelectedValue().toString());
        messageChannel.setChannel(name);

        ObjSender username = new ObjSender();
        username.setUsername(ClientCache.getInstance().getUsername());
        messageChannel.setSender(username);


        try {
            webHandler.currentSession.getBasicRemote().sendText(gson.toJson(messageChannel));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void messagePrivateSend(WebHandler webHandler, Client gui, String recipient){
        Gson gson = new Gson();
        MessagePrivate messagePrivate = new MessagePrivate();


        ObjMessage content = new ObjMessage();
        content.setContent(gui.guiMessage.getText().substring((4+recipient.length())));
        messagePrivate.setMessage(content);

        ObjChannel name = new ObjChannel();
        name.setName(recipient);
        messagePrivate.setChannel(name);

        ObjSender username = new ObjSender();
        username.setUsername(ClientCache.getInstance().getUsername());
        messagePrivate.setSender(username);

        try {
            webHandler.currentSession.getBasicRemote().sendText(gson.toJson(messagePrivate));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-------------------------//
    // Channel Functionality //
    //-------------------------//
    public void initializeDefaultChannel(WebHandler webHandler, Client gui) {
        Gson gson = new Gson();
        ChannelJoin channelJoin = new ChannelJoin();

        channelJoin.setMessage("#Default_Channel");
        gui.listModel.addElement(channelJoin.getMessage());
        gui.channelList.setSelectedIndex(gui.listModel.indexOf("#Default_Channel"));
        try {
            webHandler.currentSession.getBasicRemote().sendText(gson.toJson(channelJoin));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClientCache.getInstance().canIJoin = false;
    }
    public void createChannel(WebHandler webHandler, String channel) {
        Gson gson = new Gson();
        ChannelCreate channelCreate = new ChannelCreate();
        channelCreate.setName(channel);
        channelCreate.setDisplay_name(channel);
        try {
            webHandler.currentSession.getBasicRemote().sendText(gson.toJson(channelCreate));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void joinChannel(WebHandler webHandler, String channel) {
        if (ClientCache.getInstance().canIJoin) {
            Gson gson = new Gson();
            ChannelJoin channelJoin = new ChannelJoin();
            channelJoin.setMessage(channel);
            try {
                webHandler.currentSession.getBasicRemote().sendText(gson.toJson(channelJoin));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void partChannel(WebHandler webHandler, Client gui, String channel) {
        Gson gson = new Gson();
        ChannelPart channelPart = new ChannelPart();
        channelPart.setMessage(channel);
        gui.listModel.removeElement(channelPart.getMessage());
        try {
            webHandler.currentSession.getBasicRemote().sendText(gson.toJson(channelPart));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Displays logged messages
    public void messageDisplayLogged(JTextPane display, ArrayList<String> cachedLog) {
        StyledDocument document = (StyledDocument) display.getDocument();
        Style defaultStyle = document.addStyle("defaultStyle", null);

        try {
            for (int i = 0; i < cachedLog.size(); i++) {
                document.insertString(document.getLength(), EmojiParser.parseToUnicode(cachedLog.get(i)) + "\n", defaultStyle);
            }
            document.insertString(document.getLength(), "^--------------------Last-session--------------------^" + "\n", defaultStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    //-----------------------------//
    // Miscellaneous Functionality //
    //-----------------------------//

    //Changes views between the different screens.
    public void changeView(Client gui, Login loginScreen, Registration regScreen) {
        loadLog();
        messageDisplayLogged(gui.guiDisplay, ClientCache.getInstance().log);
        loginScreen.loginFrame.dispose();
        regScreen.registrationFrame.dispose();
        gui.guiFrame.setVisible(true);
    }

    //Updates user list
    public void refreshUsers(Client gui) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String usr = "";
                for (int i = 0; i < ClientCache.getInstance().onlineUsers.size(); i++) {
                    if (!usr.contains(ClientCache.getInstance().onlineUsers.get(i))) {
                        usr = usr.concat(ClientCache.getInstance().onlineUsers.get(i));
                        usr = usr.concat("\n");
                    }
                }
                gui.displayUsers.setText(usr);
            }
        },0,1000);
    }

    //Pushes any new messages to screen
    public void refreshMessages(Client gui) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (ClientCache.getInstance().printedMessages < ClientCache.getInstance().totalMessages) {
                    StyledDocument document = (StyledDocument) ClientCache.getInstance().panes.get(gui.channelList.getSelectedIndex()).getDocument();
                    Style defaultStyle = document.addStyle("defaultStyle", null);
                    String msg = EmojiParser.parseToUnicode(ClientCache.getInstance().msg+"\n");
                    try {
                        document.insertString(document.getLength(), msg, defaultStyle);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    ClientCache.getInstance().printedMessages++;
                }
            }
        },0,100);
    }

    //Writes cached log into logfile
    public void logWrite() {
        File file = new File("log.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            else if (file.exists()) {
                FileWriter fw = new FileWriter("log.txt");
                BufferedWriter bw = new BufferedWriter(fw);
                for (int i = 0; i < ClientCache.getInstance().log.size(); i++) {
                    bw.write(""+ ClientCache.getInstance().log.get(i));
                    bw.newLine();
                }
                bw.close();
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Reads content of logfile into cache
    public void loadLog() {
        String line = "";
        int placeInStorage = 0;
        try {
            FileReader fr = new FileReader("log.txt");
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                ClientCache.getInstance().log.add(placeInStorage, line);
                placeInStorage++;
            }
            fr.close();
            br.close();
        } catch (IOException e) {
            System.out.println("Log does not exist or could not be loaded.");
        }
        System.out.println("Log has successfully been loaded");
    }

    //Sets client theme depending on system
    public void setLookAndFeel(JFrame frame, boolean setVisible) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(frame);
        frame.setVisible(setVisible);
    }
}
