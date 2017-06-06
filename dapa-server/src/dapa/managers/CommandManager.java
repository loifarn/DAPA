package dapa.managers;

import dapa.messagetypes.*;
import dapa.server.ServerSession;

import javax.websocket.Session;
import java.util.Date;

public class CommandManager {
    private MessageManager manager = new MessageManager();
    private DatabaseManager dbManager = new DatabaseManager();
    private ConnectionManager connectionManager = ConnectionManager.getInstance();


    /**
     * Used to process the message received by the /chat endpoint and
     * assigns the correct manager to the correct action.
     *
     * @param session The server session the message is at
     * @param message The actual message received from the client
     * @return Returns the message to send back to the client.
     */
    public String processMessage(Session session, String message) {
        MessageType type = manager.getMessageType(message);
        switch (type) {
            case ERROR:
                return onError(session, message);
            case REG:
                return onReg(session, message);
            case LOGIN:
                return onLogin(session, message);
        }
        if (connectionManager.isAuthenticated(session)) {
            ServerSession serverSession = connectionManager.getServerSessionFromSession(session);
            switch (type) {
                case CREATE:
                    return onCreate(session, message, serverSession);
                case JOIN:
                    return onJoin(session, message, serverSession);
                case PART:
                    return onPart(session, message, serverSession);
                case PRIVMSG:
                    return onPrivmsg(session, message, serverSession);
                case CHANMSG:
                    return onChanmsg(session, message, serverSession);
                default:
                    return manager.CreateSimpleMessage(MessageType.ERROR, "Unrecognized message type.");
            }
        }
        return manager.CreateSimpleMessage(MessageType.ERROR, "You must be authenticated to send messages");
    }

    /**
     * Handles messages of type CHANMSG.
     * @param session Current websocket session.
     * @param message String of incoming message in json format.
     * @param serverSession ServerSession attatched to the websocket session
     * @return Processed message result.
     */
    private String onChanmsg(Session session, String message, ServerSession serverSession) {
        Srvmsg channelMessage = (Srvmsg) manager.convert(message, Srvmsg.class);
        if (serverSession.isJoined(channelMessage.getChannel().getName())) {
            Message cMsg = channelMessage.getMessage();
            Channel cChannel = dbManager.getChannel(channelMessage.getChannel().getName());
            channelMessage.setChannel(cChannel);
            cMsg.setId(IdManager.getInstance().generateID());
            cMsg.setCreated(new Date());
            channelMessage.setType(MessageType.CHANMSG);
            User user = connectionManager.getUser(session);
            channelMessage.setSender(user);
            channelMessage.setMessage(cMsg);
            connectionManager.broadcastMessage(channelMessage, user.getUsername());
            return manager.convert(channelMessage);
        } else {
            return manager.CreateSimpleMessage(MessageType.ERROR, "You can not send a message to a channel you have not joined");
        }
    }
    /**
     * Handles messages of type PRIVMSG.
     * @param session Current websocket session.
     * @param message String of incoming message in json format.
     * @param serverSession ServerSession attatched to the websocket session
     * @return Processed message result.
     */
    private String onPrivmsg(Session session, String message, ServerSession serverSession) {
        Srvmsg privateMessage = (Srvmsg) manager.convert(message, Srvmsg.class);
        if (connectionManager.userIsOnline(privateMessage.getChannel().getName())) {
            Message pMsg = privateMessage.getMessage();
            pMsg.setId(IdManager.getInstance().generateID());
            pMsg.setCreated(new Date());
            privateMessage.setType(MessageType.PRIVMSG);
            privateMessage.setSender(connectionManager.getUser(session));
            privateMessage.setMessage(pMsg);
            connectionManager.sendPrivateMessage(privateMessage);
            return manager.convert(privateMessage);
        } else {
            return manager.CreateSimpleMessage(MessageType.ERROR, "You can not send messages to a use that is offline");
        }
    }
    /**
     * Handles messages of type PART.
     * @param session Current websocket session.
     * @param message String of incoming message in json format.
     * @param serverSession ServerSession attatched to the websocket session
     * @return Processed message result.
     */
    private String onPart(Session session, String message, ServerSession serverSession) {
        Part part = (Part) manager.convert(message, Part.class);
        if (!serverSession.isJoined(part.getMessage())) {
            return manager.CreateSimpleMessage(MessageType.ERROR, "Channel " + part.getMessage() + " is not joined, can not leave channel");
        } else {
            if (serverSession.partChannel(part.getMessage())) {
                return manager.CreateSimpleMessage(MessageType.PART, "Left channel " + part.getMessage());
            }
            return manager.CreateSimpleMessage(MessageType.ERROR, "Failed to leave channel " + part.getMessage());
        }
    }
    /**
     * Handles messages of type JOIN.
     * @param session Current websocket session.
     * @param message String of incoming message in json format.
     * @param serverSession ServerSession attatched to the websocket session
     * @return Processed message result.
     */
    private String onJoin(Session session, String message, ServerSession serverSession) {
        Join join = (Join) manager.convert(message, Join.class);
        Channel joinchannel = dbManager.getChannel(join.getMessage());
        if (joinchannel == null) {
            return manager.CreateSimpleMessage(MessageType.ERROR, "Unable to join channel. Channel not found.");
        } else {
            if (serverSession.isJoined(joinchannel.getName())) {
                return manager.CreateSimpleMessage(MessageType.ERROR, "Channel " + joinchannel.getName() + " is already joined");
            } else {
                if (serverSession.joinChannel(joinchannel)) {
                    return manager.CreateSimpleMessage(MessageType.JOIN, "Joined channel " + joinchannel.getName());
                }
                return manager.CreateSimpleMessage(MessageType.ERROR, "Failed to join the channel " + joinchannel.getName());
            }
        }
    }
    /**
     * Handles messages of type CREATE.
     * @param session Current websocket session.
     * @param message String of incoming message in json format.
     * @param serverSession ServerSession attatched to the websocket session
     * @return Processed message result.
     */
    private String onCreate(Session session, String message, ServerSession serverSession) {
        Channel Cchannel = (Channel) manager.convert(message, Channel.class);
        if (Cchannel == null) {
            return manager.CreateSimpleMessage(MessageType.ERROR, "Failed to create channel, message format not correct");
        }
        Channel CreatedChannel = dbManager.createChannel(Cchannel);
        if (CreatedChannel == null) {
            return manager.CreateSimpleMessage(MessageType.ERROR, "Failed to create channel, the channel might exist");
        } else {
            return manager.CreateSimpleMessage(MessageType.CREATE, "Successfully created channel " + CreatedChannel.getName());
        }
    }
    /**
     * Handles messages of type LOGIN.
     * @param session Current websocket session.
     * @param message String of incoming message in json format.
     * @return Processed message result.
     */
    private String onLogin(Session session, String message) {
        if (connectionManager.isAuthenticated(session)) {
            return manager.CreateSimpleMessage(MessageType.ERROR, "You are already authenticated on this server.");
        }
        connectionManager.protectConnection(session, false);
        Login login = (Login) manager.convert(message, Login.class);
        User user = dbManager.getUser(login.getUsername(), login.getPassword());
        if (user != null) {
            if (connectionManager.userIsOnline(user.getUsername())) {
                return manager.CreateSimpleMessage(MessageType.ERROR, "You are already signed in");
            }
            connectionManager.unprotectConnection(session, true);
            connectionManager.connectUserToSession(user, session);
            Logger.getInstace().info(user.getUsername() + " signed in");
            return manager.CreateSimpleMessage(MessageType.WELCOME, "Welcome to the server, you are signed in as " + user.getUsername());
        } else {
            connectionManager.unprotectConnection(session, false);
            return manager.CreateSimpleMessage(MessageType.ERROR, "Invalid login credentials");
        }
    }
    /**
     * Handles messages of type REG.
     * @param session Current websocket session.
     * @param message String of incoming message in json format.
     * @return Processed message result.
     */
    private String onReg(Session session, String message) {
        if (connectionManager.isAuthenticated(session)) {
            return manager.CreateSimpleMessage(MessageType.ERROR, "You are already authenticated on this server.");
        }
        connectionManager.protectConnection(session, false);
        RegMessage msg = (RegMessage) manager.convert(message, RegMessage.class);
        User regUser = dbManager.registerUser(msg.getDisplay_Name(), msg.getUsername(), msg.getPassword());
        if (regUser != null) {
            connectionManager.unprotectConnection(session, true);
            connectionManager.connectUserToSession(regUser, session);
            return manager.CreateSimpleMessage(MessageType.WELCOME, "Welcome to the server, you are now signed up as " + msg.getUsername());
        } else {
            connectionManager.unprotectConnection(session, false);
            return manager.CreateSimpleMessage(MessageType.ERROR, "Failed to register user, username might be taken");
        }
    }
    /**
     * Handles messages that could not be parsed into a format recognized by the server.
     * @param session Current websocket session.
     * @param message String of incoming message in json format.
     * @return Processed message result.
     */
    private String onError(Session session, String message) {
        return manager.CreateSimpleMessage(MessageType.ERROR, "Message type could not be handled by the server");
    }
}
