package dapa.messagetypes;

public class User {
    public User(String id, String username, String display_name){
        this.display_name = display_name;
        this.username = username;
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String id;
    private String display_name;
    private String username;
}
