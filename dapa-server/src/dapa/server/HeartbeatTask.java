package dapa.server;

import dapa.managers.ConnectionManager;
import dapa.managers.Logger;
import dapa.managers.MessageManager;
import dapa.messagetypes.MessageType;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Dev on 28.04.2017.
 */
public class HeartbeatTask extends TimerTask {
    @Override
    public void run() {
        try {
            // Logger.getInstace().info("Starting Authentication check");
            ConnectionManager conMan = ConnectionManager.getInstance();
            MessageManager messageManager = new MessageManager();
            ConcurrentHashMap<Session, ServerSession> sessions = conMan.getServerSessions();
            for (Iterator<Map.Entry<Session, ServerSession>> iter = sessions.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<Session, ServerSession> entry = iter.next();
                ServerSession session = entry.getValue();
                if (session == null) {
                    Logger.getInstace().warning("Server session is null, failed to send heartbeat.");
                    continue;
                }
                try {
                    if (session.getSession().isOpen()) {
                        session.getSession().getBasicRemote().sendText(messageManager.CreateSimpleMessage(MessageType.HEARTBEAT, "HEARTBEAT"));
                        Logger.getInstace().warning("Sending heartbeat " + session.getSession().getId());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
