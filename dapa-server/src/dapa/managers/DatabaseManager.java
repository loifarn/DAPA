package dapa.managers;

import dapa.ServerConfig;
import dapa.messagetypes.Channel;
import dapa.messagetypes.User;

import java.sql.*;

public class DatabaseManager {

    private ServerConfig config = ServerConfig.getInstance();
    private String url = "jdbc:mysql://" + config.getDBHost() + ":" + config.getDBPort() + "/" + config.getDatabase() + "?verifyServerCertificate=false&useSSL=true";
    private String db_user = config.getDBUser();
    private String db_password = config.getDBPassword();

    public DatabaseManager() {

    }

    /**
     * Checks if a username is taken by a different user
     * @param Username Username to check
     * @return true/false
     */
    public boolean isUsernameTaken(String Username) {
        try {
            boolean isTaken = false;
            Connection conn = DriverManager.getConnection(url, db_user, db_password);
            String query = "SELECT * FROM users";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String name = rs.getString("username");
                if (Username.equals(name)) {
                    isTaken = true;
                }
            }
            st.close();
            conn.close();
            return isTaken;
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }

    /**
     * Gets a user from the database
     * @param Username Username of user
     * @param Password Password for user
     * @return User object
     */
    public User getUser(String Username, String Password) {
        try {
            User user = null;
            Connection conn = DriverManager.getConnection(url, db_user, db_password);
            String query = "SELECT * FROM users";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String name = rs.getString("username");
                String pass = rs.getString("password");
                if (Username.equals(name) && Password.equals(pass)) {
                    user = new User(rs.getString("user_id"), name, rs.getString("display_name"));
                    break;
                }
            }
            st.close();
            conn.close();
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a new user to the database
     * @param DisplayName DisplayName of the user
     * @param Username Username of the user
     * @param Password Password of the user
     * @return User object of the created user
     */
    public User registerUser(String DisplayName, String Username, String Password) {
        try {
            boolean isTaken = this.isUsernameTaken(Username);
            if (isTaken) {
                return null;
            }
            String Id = IdManager.getInstance().generateID();
            Connection conn = DriverManager.getConnection(url, db_user, db_password);
            String query = " INSERT INTO users (user_id, display_name,username, password)"
                    + " VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, Id);
            preparedStmt.setString(2, DisplayName);
            preparedStmt.setString(3, Username);
            preparedStmt.setString(4, Password);
            preparedStmt.execute();
            conn.close();
            return new User(Id, Username, DisplayName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Gets a channel from the database
     * @param channelName Channel to get
     * @return Channel object
     */
    public Channel getChannel(String channelName) {
        try {
            Channel channel = null;
            Connection conn = DriverManager.getConnection(url, db_user, db_password);
            String query = "SELECT * FROM channels";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String channel_db_name = rs.getString("name");
                if (channelName.equals(channel_db_name)) {
                    channel = new Channel(rs.getString("channel_id"), rs.getString("name"), rs.getString("display_name"));
                    break;
                }
            }
            st.close();
            conn.close();
            return channel;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a new channel to the database
     * @param channel Channel object to add
     * @return Created channel object
     */
    public Channel createChannel(Channel channel) {
        try {
            channel.setId(IdManager.getInstance().generateID());
            if (getChannel(channel.getName()) == null) {
                Connection conn = DriverManager.getConnection(url, db_user, db_password);
                String query = " INSERT INTO channels (channel_id, name,display_name)"
                        + " VALUES (?, ?, ?)";
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString(1, channel.getId());
                preparedStmt.setString(2, channel.getName());
                preparedStmt.setString(3, channel.getDisplay_name());
                preparedStmt.execute();
                conn.close();
                return channel;
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
