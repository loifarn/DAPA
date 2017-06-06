package dapa.messagetypes;

import dapa.interfaces.IMessage;

public class Channel implements IMessage {
    private String id;
    private String name;
    private String display_name;

    public Channel(String id, String name, String display_name) {
        this.id = id;
        this.name = name;
        if (display_name == null) {
            this.display_name = name;
        } else {
            this.display_name = display_name;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }
}
