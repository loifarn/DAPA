package web.Requests;

public class ChannelCreate {
    //Declaring private objects and variables
    private String type = "CREATE";
    private String name;
    private String display_name;

    //Getters and Setters
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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
