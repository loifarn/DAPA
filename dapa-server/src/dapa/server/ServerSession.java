package dapa.server;

import dapa.managers.ConnectionManager;
import dapa.managers.Logger;
import dapa.messagetypes.Channel;
import dapa.messagetypes.User;

import javax.websocket.Session;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ServerSession {
    private Session session;
    private Date connectionTime = new Date();
    private ConcurrentHashMap<String, Channel> joinedChannels = new ConcurrentHashMap<>();


    private User user;
    private boolean isAuthenticated = false;
    private boolean isProtectedFromClose = false;

    public boolean isProtectedFromClose() {
        return isProtectedFromClose;
    }

    public void setProtectedFromClose(boolean protectedFromClose) {
        isProtectedFromClose = protectedFromClose;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Date getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(Date connectionTime) {
        this.connectionTime = connectionTime;
    }

    public boolean joinChannel(Channel channel) {
        if (channel == null) {
            Logger.getInstace().warning("Trying to join channel, but channel is not defined");
            return false;
        }
        joinedChannels.put(channel.getName(), channel);
        try {
            ConnectionManager.getInstance().broadcastWho(channel.getName(), false, true, this.user.getUsername());
            ConnectionManager.getInstance().broadcastWho(channel.getName(), true, false, this.user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean partChannel(String channelName) {
        if (!isJoined(channelName)) return false;
        joinedChannels.remove(channelName);
        try {
            ConnectionManager.getInstance().broadcastWho(channelName, false, false, this.user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isJoined(String channelName) {
        return joinedChannels.containsKey(channelName);
    }

    public void partAllChannels() {
        for (Iterator<Map.Entry<String, Channel>> iter = joinedChannels.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, Channel> entry = iter.next();
            this.partChannel(entry.getKey());
        }
    }
}

