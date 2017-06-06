package dapa;

public class ServerConfig {
    private static ServerConfig serverConfig = new ServerConfig();

    public static ServerConfig getInstance() {
        return serverConfig;
    }

    private int Port;
    private String Host = "";
    private String DBHost = "";
    private String DBUser = "";
    private int DBPort;
    private String DBPassword = "";
    private String Database = "";

    public int getPort() {
        return Port;
    }

    public void setPort(int port) {
        Port = port;
    }

    public String getHost() {
        return Host;
    }

    public void setHost(String host) {
        Host = host;
    }

    public String getDBHost() {
        return DBHost;
    }

    public void setDBHost(String DBHost) {
        this.DBHost = DBHost;
    }

    public String getDBUser() {
        return DBUser;
    }

    public void setDBUser(String DBUser) {
        this.DBUser = DBUser;
    }

    public int getDBPort() {
        return DBPort;
    }

    public void setDBPort(int DBPort) {
        this.DBPort = DBPort;
    }

    public String getDBPassword() {
        return DBPassword;
    }

    public void setDBPassword(String DBPassword) {
        this.DBPassword = DBPassword;
    }


    public static void setInstance(ServerConfig config) {
        serverConfig = config;
    }

    public String getDatabase() {
        return Database;
    }

    public void setDatabase(String database) {
        Database = database;
    }
}
