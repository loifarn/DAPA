package web.Requests;

import web.Objects.ObjChannel;
import web.Objects.ObjMessage;
import web.Objects.ObjSender;

public class MessagePrivate {
    //Declaring private objects and variables
    private String type = "PRIVMSG";
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
    public String getDateCreated() {
        /*
        Full date is a subObject inside message, it is retrieved using message.GetCreated
        then formatted and returned in this function.
        */
        String tmp = message.getCreated();
        tmp = tmp.substring(tmp.length()-10,tmp.length());
        tmp = "["+tmp+"]";
        return tmp;
    }

}
