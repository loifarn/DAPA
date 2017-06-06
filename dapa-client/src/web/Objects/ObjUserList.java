package web.Objects;

public class ObjUserList {
    //Declaring private objects and variables
    private String type = "WHO";
    private String status;
    private String channel;
    private String[] users;

    //Getters and Setters
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getChannel() {
        return channel;
    }
    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String[] getUsers() {
        return users;
    }
    public void setUsers(String[] users) {
        this.users = users;
    }
}
