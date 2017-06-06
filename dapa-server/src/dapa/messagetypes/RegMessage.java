package dapa.messagetypes;

import dapa.interfaces.IMessage;


public class RegMessage implements IMessage {
    private String username;
    private String password;
    private String display_name;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplay_Name() {
        return display_name;
    }

    public void setDisplay_Name(String display_Name) {
        this.display_name = display_name;
    }
}
