package web.Requests;

import web.Objects.ObjChannel;
import web.Objects.ObjMessage;
import web.Objects.ObjSender;

public class MessageChannel {
    //Declaring private objects and variables
    private String type = "CHANMSG";
    private ObjMessage message;
    private ObjChannel channel;
    private ObjSender sender;

    //Getters and Setters
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public ObjMessage getMessage() {
        return message;
    }
    public void setMessage(ObjMessage message) {
        this.message = message;
    }

    public ObjChannel getChannel() {
        return channel;
    }
    public void setChannel(ObjChannel channel) {
        this.channel = channel;
    }

    public ObjSender getSender() {
        return sender;
    }
    public void setSender(ObjSender sender) {
        this.sender = sender;
    }

    //Getters for content nested in objects.
    public String getContent() {
        return message.getContent();
    }
    public String getUsername() {
        return sender.getUsername();
    }
    public String getName() {
        return channel.getName();
    }
    public String getDisplay_name() {
        return sender.getDisplay_name();
    }

    //Retrieves date from object 'message', formats it and returns it.
    public String getDateCreated() {
        String tmp = message.getCreated();
        tmp = tmp.substring(tmp.length()-10,tmp.length());
        tmp = "["+tmp+"]";
        return tmp;
    }

}
