package dapa.messagetypes;

import dapa.interfaces.IMessage;

import java.util.Date;

public class Message implements IMessage {
    public Message(String id, String content, Date created) {
        this.id = id;
        this.content = content;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    private String id;
    private String content;
    private Date created;
}
