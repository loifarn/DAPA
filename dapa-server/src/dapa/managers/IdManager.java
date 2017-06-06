package dapa.managers;

import java.util.UUID;

public class IdManager {
    private static IdManager idManager = new IdManager();

    public static IdManager getInstance() {
        return idManager;
    }

    /**
     * Generates a random id.
     *
     * @return UUID String
     */
    public String generateID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
