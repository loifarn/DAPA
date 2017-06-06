package dapa.messagetypes;

import dapa.interfaces.IMessage;

public class SimpleMessage implements IMessage {
    public MessageType type;
    public String message;

    public SimpleMessage(MessageType type, String message) {
        this.type = type;
        this.message = message;
    }
}
