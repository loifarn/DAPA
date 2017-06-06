package dapa.server;

import dapa.messagetypes.MessageType;
import dapa.managers.ConnectionManager;
import dapa.managers.Logger;
import dapa.managers.MessageManager;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthCheckerTask extends TimerTask {
    @Override
    public void run() {
        try {
            ConnectionManager conMan = ConnectionManager.getInstance();
            MessageManager messageManager = new MessageManager();
            ConcurrentHashMap<Session, ServerSession> unAuthenticatedSessions = conMan.getServerSessions();
            for (Iterator<Map.Entry<Session, ServerSession>> iter = unAuthenticatedSessions.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<Session, ServerSession> entry = iter.next();
                ServerSession session = entry.getValue();
                if (session == null) {
                    Logger.getInstace().warning("Trying to authenticate a session missing the ServerSession, closing");
                    entry.getKey().getBasicRemote().sendText(messageManager.CreateSimpleMessage(MessageType.ERROR, "Internal error"));
                    ConnectionManager.getInstance().closeSession(entry.getKey());
                    continue;
                }
                if (session.getConnectionTime().getTime() < (new Date().getTime() - 10000)) {
                    if (!session.isAuthenticated() && !session.isProtectedFromClose()) {
                        try {
                            String closeMessage = "No authentication provided within the given time frame";
                            if (session.getSession().isOpen()) {
                                session.getSession().getBasicRemote().sendText(messageManager.CreateSimpleMessage(MessageType.CLOSE, closeMessage));
                                session.getSession().close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, closeMessage));
                                Logger.getInstace().warning("Closing session " + session.getSession().getId() + " : Authentication timeout");
                            } else {
                                Logger.getInstace().warning("Session " + session.getSession().getId() + " is closed, removing");
                            }
                            unAuthenticatedSessions.remove(entry.getKey());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
