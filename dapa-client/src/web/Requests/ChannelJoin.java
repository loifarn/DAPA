package web.Requests;

public class ChannelJoin {
    //Declaring private objects and variables
    private String type = "JOIN";
    private String message;

    //Getters and Setters
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }


}
