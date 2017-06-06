package web.Utilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ParseMessage {
    //Parses incoming messages to a string.
    public static String parse(String message){
        try {
            JsonElement jelement = new JsonParser().parse(message);
            JsonObject jobject = jelement.getAsJsonObject();
            String typeString = jobject.get("type").getAsString();
            return typeString;
        } catch (IllegalArgumentException e) {
            return "ERROR";
        }
    }
}