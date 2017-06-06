package dapa.managers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dapa.interfaces.IMessage;
import dapa.messagetypes.*;

import java.lang.reflect.Type;

public class MessageManager {
    Gson gson = new Gson();

    /**
     * Generates a simple JSON formatted message.
     * @param type Message type
     * @param message Message
     * @return JSON String
     */
    public String CreateSimpleMessage(MessageType type, String message) {
        return gson.toJson(new SimpleMessage(type, message));
    }

    /**
     * Converts a JSON string into selected object type
     * @param message String to decode
     * @param type Type to decode to
     * @return Decoded object
     */
    public IMessage convert(String message, Type type) {
        return gson.fromJson(message, type);
    }

    /**
     * Encodes a message to JSON
     * @param message Object to encode.
     * @return JSON String
     */
    public String convert(IMessage message) {
        return gson.toJson(message);
    }

    /**
     * Get the type param from JSON message
     * @param message JSON message to parse.
     * @return MessageType enum
     */
    public MessageType getMessageType(String message) {
        try {
            JsonElement jelement = new JsonParser().parse(message);
            JsonObject jobject = jelement.getAsJsonObject();
            String typeString = jobject.get("type").getAsString();
            return MessageType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            return MessageType.ERROR;
        }

    }
}
