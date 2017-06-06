package dapa.managers;

import dapa.interfaces.IMessage;
import dapa.messagetypes.*;
import dapa.server.ServerSession;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private static ConnectionManager connectionManager = new ConnectionManager();

    // All connected websocket sessions with their server session.
    private ConcurrentHashMap<Session, ServerSession> serverSessions = new ConcurrentHashMap<>();


    public ConcurrentHashMap<Session, ServerSession> getServerSessions() {
        return serverSessions;
    }

    public static ConnectionManager getInstance() {
        return connectionManager;
    }

    /**
     * Adds a new connection to server storage.
     *
     * @param session The current connected session
     */
    public void newConnection(Session session) {
        ServerSession ses = new ServerSession();
        ses.setConnectionTime(new Date());
        ses.setAuthenticated(false);
        ses.setSession(session);
        if (this.serverSessions.contains(session)) {
            Logger.getInstace().warning("The session " + session.getId() + " is already connected");
            return;
        }
        this.serverSessions.putIfAbsent(session, ses);
    }

    /**
     * Check if the current session is authenticated
     *
     * @param session The current connected session
     * @return true for authenticated, false for not.
     */
    public synchronized boolean isAuthenticated(Session session) {
        ServerSession serverSession = getServerSessionFromSession(session);
        return serverSession != null && serverSession.isAuthenticated();
    }

    /**
     * Closes a session and removes it from the server storage.
     *
     * @param session The session to close.
     */
    public void closeSession(Session session) {
        ServerSession serverSession = getServerSessionFromSession(session);
        serverSession.partAllChannels();
        serverSessions.remove(session);
    }

    /**
     * Protects a session from being closed by the authenticon timer.
     * Being used in functions that might take longer than 10 seconds,
     * like calls to the database.
     *
     * @param session         The websocket session to alter.
     * @param isAuthenticated sets if the session is authenticated
     */
    public void protectConnection(Session session, boolean isAuthenticated) {
        modifyConnection(session, true, isAuthenticated);
    }

    /**
     * Sets a session back to unprotected after being protected.
     *
     * @param session         The websocket session to alter.
     * @param isAuthenticated Sets if the session is authenticated.
     */
    public void unprotectConnection(Session session, boolean isAuthenticated) {
        modifyConnection(session, false, isAuthenticated);
    }

    /**
     * Parent handler for protectConnection and unprotectConnection.
     *
     * @param session         The webspcket session to alter.
     * @param isProtected     Sets if the session is protected.
     * @param isAuthenticated Sets if the session is authenticated.
     */
    private void modifyConnection(Session session, boolean isProtected, boolean isAuthenticated) {
        ServerSession serverSession = getServerSessionFromSession(session);
        serverSession.setProtectedFromClose(isProtected);
        serverSession.setAuthenticated(isAuthenticated);
    }

    /**
     * Get the internal server session from the websocket session.
     *
     * @param session The current connected websocket session.
     * @return Returns a server session.
     */
    public ServerSession getServerSessionFromSession(Session session) {
        for (Iterator<Map.Entry<Session, ServerSession>> iter = serverSessions.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<Session, ServerSession> entry = iter.next();
            if (entry.getKey().equals(session))
                return entry.getValue();
        }
        return null;
    }

    /**
     * Sends a message to a connected user.
     *
     * @param privateMessage Message object.
     */
    public void sendPrivateMessage(Srvmsg privateMessage) {
        try {
            MessageManager manager = new MessageManager();
            String name = privateMessage.getChannel().getName();
            ServerSession serverSession = getServerSessionFromUsername(name);
            Session session = serverSession != null ? serverSession.getSession() : null;
            if (session != null) {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(manager.convert(privateMessage));
                } else {
                    Logger.getInstace().error("Server session closed, failed to send message");
                }
            }
        } catch (Exception e) {
            Logger.getInstace().error("Failed to send private message");
        }
    }

    /**
     * Sends a message to all connected users in a channel.
     *
     * @param channel The channel to send the message to.
     * @param message The object containing the message.
     */
    public void broadcastMessage(String channel, IMessage message) {
        try {
            MessageManager manager = new MessageManager();
            for (Iterator<Map.Entry<Session, ServerSession>> iter = serverSessions.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<Session, ServerSession> entry = iter.next();
                ServerSession serverSession = entry.getValue();
                if (serverSession != null) {
                    if (serverSession.isJoined(channel)) {
                        if (serverSession.getSession().isOpen()) {
                            serverSession.getSession().getBasicRemote().sendText(manager.convert(message));
                        } else {
                            Logger.getInstace().error("Session not open, failed to send message");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to all connected users..
     *
     * @param message The object containing the message.
     */
    public void broadcastMessage(Srvmsg message, String username) {
        try {
            MessageManager manager = new MessageManager();
            for (Iterator<Map.Entry<Session, ServerSession>> iter = serverSessions.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<Session, ServerSession> entry = iter.next();
                ServerSession serverSession = entry.getValue();
                if (serverSession != null) {
                    if (serverSession.isJoined(message.getChannel().getName()) && !serverSession.getUser().getUsername().equals(username)) {
                        if (serverSession.getSession().isOpen()) {
                            serverSession.getSession().getBasicRemote().sendText(manager.convert(message));
                        } else {
                            Logger.getInstace().error("Session not open, failed to send message");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcasts userlist to users on connect or on disconnect.
     *
     * @param channel       The channel to send the userlist to.
     * @param isJoin        If the user joined the channel.
     * @param isChannelJoin If the user joins the channel for the first time.
     * @param username      Username of the user that joins the channel.
     * @throws IOException
     */
    public void broadcastWho(String channel, boolean isJoin, boolean isChannelJoin, String username) throws IOException {
        ServerSession serverSession = getServerSessionFromUsername(username);
        MessageManager messageManager = new MessageManager();
        String[] usersInChannel = ConnectionManager.getInstance().getUsersInChannel(channel);
        if (isChannelJoin) {
            Who firstJoin = new Who();
            firstJoin.setStatus("USERS");
            firstJoin.setType(MessageType.WHO);
            firstJoin.setChannel(channel);
            firstJoin.setUsers(usersInChannel);
            serverSession.getSession().getBasicRemote().sendText(messageManager.convert(firstJoin));
            return;
        }
        if (isJoin) {
            Who singleJoin = new Who();
            singleJoin.setStatus("JOIN");
            singleJoin.setType(MessageType.WHO);
            singleJoin.setChannel(channel);
            singleJoin.setUsers(new String[]{username});
            for (String user : usersInChannel) {
                if (!user.equals(username)) {
                    ConnectionManager.getInstance().getServerSessionFromUsername(user).
                            getSession().getBasicRemote().sendText(messageManager.convert(singleJoin));
                }
            }
        } else {
            Who singlePart = new Who();
            singlePart.setStatus("PART");
            singlePart.setType(MessageType.WHO);
            singlePart.setChannel(channel);
            singlePart.setUsers(new String[]{username});
            for (String user : usersInChannel) {
                if (!user.equals(username)) {
                    ConnectionManager.getInstance().getServerSessionFromUsername(user).
                            getSession().getBasicRemote().sendText(messageManager.convert(singlePart));
                }
            }
        }
    }

    /**
     * Get the internal server session from the username.
     *
     * @param username The username to get session for.
     * @return Returns a server session.
     */
    private ServerSession getServerSessionFromUsername(String username) {
        for (Iterator<Map.Entry<Session, ServerSession>> iter = serverSessions.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<Session, ServerSession> entry = iter.next();
            ServerSession serverSession = entry.getValue();
            if (serverSession != null) {
                User user = serverSession.getUser();
                if (user != null) {
                    if (user.getUsername().equals(username)) {
                        return entry.getValue();
                    }
                }
            }

        }
        return null;
    }

    /**
     * Binds a user object to the websocket and server session.
     *
     * @param user    The user to connect
     * @param session The websocket session to conenct
     */
    public void connectUserToSession(User user, Session session) {
        ServerSession serverSession = getServerSessionFromSession(session);
        serverSession.setUser(user);
    }

    /**
     * Gets a user object for the current websocket session
     *
     * @param session Current websocket session
     * @return User object
     */
    public User getUser(Session session) {
        ServerSession serverSession = getServerSessionFromSession(session);
        return serverSession.getUser();
    }

    /**
     * Gets the online status for a user.
     *
     * @param username Username of the user
     * @return true for online, false for offline.
     */
    public boolean userIsOnline(String username) {
        for (Iterator<Map.Entry<Session, ServerSession>> iter = serverSessions.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<Session, ServerSession> entry = iter.next();
            ServerSession serverSession = entry.getValue();
            if (serverSession != null) {
                if (serverSession.getUser() != null) {
                    if (serverSession.getUser().getUsername().equals(username)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Get all users joined in channel
     *
     * @param channel Name of the channel
     * @return Array of users
     */
    public String[] getUsersInChannel(String channel) {
        ArrayList<String> users = new ArrayList<>();
        for (Iterator<Map.Entry<Session, ServerSession>> iter = serverSessions.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<Session, ServerSession> entry = iter.next();
            ServerSession serverSession = entry.getValue();
            if (serverSession != null) {
                User user = serverSession.getUser();
                if (user != null) {
                    if (serverSession.isJoined(channel)) {
                        users.add(user.getUsername());
                    }
                }
            }
        }
        return users.toArray(new String[0]);
    }
}