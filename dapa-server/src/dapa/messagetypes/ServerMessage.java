package dapa.messagetypes;

import dapa.interfaces.IMessage;

public class ServerMessage implements IMessage {
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    private Channel channel;
    private User sender;
    private Message message;

    public ServerMessage(Channel channel, User sender, Message message) {
        this.channel = channel;
        this.sender = sender;
        this.message = message;
    }
    public ServerMessage(){

    }
}