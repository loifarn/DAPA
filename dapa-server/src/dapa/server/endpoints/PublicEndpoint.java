package dapa.server.endpoints;

import dapa.messagetypes.*;
import dapa.managers.*;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;


@ServerEndpoint(value = "/chat")
public class PublicEndpoint extends Endpoint {
    @OnMessage
    public String onMessage(String message, Session session) throws IOException {
        CommandManager manager = new CommandManager();
        session.setMaxIdleTimeout(-1);
        return manager.processMessage(session, message);
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        ConnectionManager.getInstance().newConnection(session);
        MessageManager manager = new MessageManager();
        try {
            session.getBasicRemote().sendText(manager.CreateSimpleMessage(MessageType.CONNECTED, "You are in a maze of twisty passages"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.getInstace().info("New session: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        thr.printStackTrace();
        super.onError(session, thr);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        ConnectionManager.getInstance().closeSession(session);
        Logger.getInstace().warning(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }
}
