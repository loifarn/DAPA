package Handlers;

import GUI.Client;
import Logic.ClientCache;
import web.Utilities.ParseMessage;
import com.google.gson.Gson;
import org.glassfish.tyrus.client.ClientManager;
import web.Requests.ChannelJoin;
import web.Requests.ChannelPart;
import web.Requests.MessageChannel;
import web.Requests.MessagePrivate;
import web.Objects.ObjUserList;
import javax.swing.*;
import javax.websocket.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class WebHandler {
    //Current session declaration os it is externally accessible.
    public Session currentSession;

    //Booleans used for checking if login/registration is allowed.
    public boolean validRegistration;
    public boolean validLogin;

    //Constructor
    public WebHandler() {
        newConnection();
    }

    private void newConnection() {
        try {
            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();
            ClientManager client = ClientManager.createClient();
            client.connectToServer(new Endpoint() {
                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    System.out.println("Closed");
                    super.onClose(session, closeReason);
                }

                @Override
                public void onError(Session session, Throwable thr) {
                    System.out.println("Some error");
                    super.onError(session, thr);
                }

                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    currentSession = session;

                    session.addMessageHandler(new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String message) {
                            Gson gson = new Gson();
                            System.out.println(message);
                            String type = ParseMessage.parse(message);
                            switch (type) {
                                case "CONNECTED":
                                    break;
                                case "WELCOME":
                                    ClientCache.getInstance().canIJoin = true;
                                    if (message.contains("signed in as")) {
                                        validLogin = true;
                                    }
                                    if (message.contains("signed up as")) {
                                        validRegistration = true;
                                        validLogin = true;
                                    }
                                    break;
                                case "CHANMSG":
                                    MessageChannel chanMSG = gson.fromJson(message, MessageChannel.class);

                                    String chanMSGFormatted = chanMSG.getDateCreated()+"[ALL]"+chanMSG.getDisplay_name()+":  "+chanMSG.getContent();

                                    ClientCache.getInstance().log.add(chanMSGFormatted);
                                    ClientCache.getInstance().msg = chanMSGFormatted;
                                    ClientCache.getInstance().totalMessages++;
                                    break;
                                case "PRIVMSG":
                                    MessagePrivate privMSG = gson.fromJson(message, MessagePrivate.class);

                                    String privMSGFormatted = privMSG.getDateCreated()+"[PM "+privMSG.getSender().getDisplay_name()+"]"+" "+":  "+privMSG.getContent();

                                    ClientCache.getInstance().log.add(privMSGFormatted);
                                    ClientCache.getInstance().msg = privMSGFormatted;
                                    ClientCache.getInstance().totalMessages++;
                                    break;
                                case "JOIN":
                                    if (!message.contains("#Default_Channel")) {
                                        ChannelJoin channelJoin = gson.fromJson(message, ChannelJoin.class);
                                        ClientCache.getInstance().newTab((Client) ClientCache.getInstance().cachedGUI,channelJoin.getMessage().substring(15));
                                    }
                                        break;
                                case "PART":
                                    if (message.contains("status") && message.contains("PART")) {
                                        //Removes users that left
                                        ObjUserList userList = gson.fromJson(message, ObjUserList.class);
                                        String[] tmpArr = userList.getUsers();
                                        List<String> newList = Arrays.asList(tmpArr);
                                        ClientCache.getInstance().onlineUsers.removeAll(newList);
                                    }
                                    if (message.contains("Left channel")) {
                                        //Removes channels that got parted
                                        ChannelPart channelPart = gson.fromJson(message,ChannelPart.class);
                                        ClientCache.getInstance().removeTab((Client) ClientCache.getInstance().cachedGUI, channelPart.getMessage().substring(13));
                                        ((Client) ClientCache.getInstance().cachedGUI).channelList.setSelectedIndex(0);
                                    }
                                break;
                                case "WHO":
                                    ClientCache.getInstance().canIJoin = true;
                                    if (message.contains("status") && message.contains("USERS")) {
                                        ObjUserList userList = gson.fromJson(message, ObjUserList.class);

                                        //Loads all online users into cache
                                        String[] tmpArr = userList.getUsers();
                                        List<String> newList = Arrays.asList(tmpArr);
                                        ClientCache.getInstance().onlineUsers.addAll(newList);
                                    }
                                    if (message.contains("status") && message.contains("JOIN")) {
                                        ObjUserList userList = gson.fromJson(message, ObjUserList.class);

                                        //Adds any user that joined
                                        String[] tmpArr = userList.getUsers();
                                        List<String> newList = Arrays.asList(tmpArr);
                                        ClientCache.getInstance().onlineUsers.addAll(newList);
                                    }
                                    if (message.contains("status") && message.contains("PART")) {
                                        //Lazy formatting out the name.

                                        String tmp = message;
                                        int start = message.indexOf("[")+2;
                                        int slutt = message.indexOf("]")-1;
                                        String name = tmp.substring(start,slutt);
                                        ClientCache.getInstance().onlineUsers.remove(name);

                                    }
                                    break;
                                case "ERROR":
                                    if (message.contains("Message type could not be handled by the server")) {
                                        JOptionPane.showMessageDialog(null,
                                                "Message type could not be handled by the server",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE,
                                                new ImageIcon("src/Resources/png32/error.png"));
                                    }
                                    if (message.contains("Failed to register user, username might be taken")) {
                                        JOptionPane.showMessageDialog(null,
                                                "Failed to register user, username might be taken",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE,
                                                new ImageIcon("src/Resources/png32/error.png"));
                                    }
                                    if (message.contains("You are already authenticated on this server.")) {
                                        JOptionPane.showMessageDialog(null,
                                                "You are already logged in on this server.",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE,
                                                new ImageIcon("src/Resources/png32/error.png"));
                                    }
                                    if (message.contains("Failed to register user, username might be taken")) {
                                        JOptionPane.showMessageDialog(null,
                                                "Failed to register user, username might be taken",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE,
                                                new ImageIcon("src/Resources/png32/error.png"));
                                    }
                                    if (message.contains("Invalid login credentials")) {
                                        JOptionPane.showMessageDialog(null,
                                                "Invalid username/password",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE,
                                                new ImageIcon("src/Resources/png32/error.png"));
                                    }

                                    if (message.contains("You can not send messages to a use that is offline")) {
                                        JOptionPane.showMessageDialog(null,
                                                "User is offline",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE,
                                                new ImageIcon("src/Resources/png32/error.png"));
                                    }
                                    if (message.contains("is not joined")) {
                                        JOptionPane.showMessageDialog(null,
                                                "You haven't joined that channel",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE,
                                                new ImageIcon("src/Resources/png32/error.png"));
                                    }
                                    if (message.contains("You must be authenticated to send messages")) {
                                        JOptionPane.showMessageDialog(null,
                                                "You must be logged in to send messages",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE,
                                                new ImageIcon("src/Resources/png32/error.png"));
                                    }
                                    if (message.contains("channel you have not joined")) {
                                        JOptionPane.showMessageDialog(null,
                                                "Channel not joined",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE,
                                                new ImageIcon("src/Resources/png32/error.png"));
                                    }
                                    if (message.contains("Channel not found")) {
                                        JOptionPane.showMessageDialog(null,
                                                "Channel not found",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE,
                                                new ImageIcon("src/Resources/png32/error.png"));
                                    }
                                    break;
                            }
                        }
                    });
                }
            }, cec, new URI("ws://dat113chat.dalenapps.no/chat"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}