package Logic;

import GUI.Client;
import javax.swing.*;
import java.util.ArrayList;

public class ClientCache {
    //Singleton initialization start
    private static ClientCache instance = null;
    protected ClientCache() {
        // Exists only to defeat instantiation.
    }
    public static ClientCache getInstance() {
        if (instance == null) {
            instance = new ClientCache();
        }
        return instance;
    }
    //Single initialization end

    //Private variables and objects for messaging.
    private String username;
    private String displayName;
    private String channelName;

    //Public variables and objects that needs to be cached.
    public ArrayList<String> onlineUsers = new ArrayList<>();
    public ArrayList<String> log = new ArrayList<>();
    public ArrayList<JTextPane> panes = new ArrayList<>();

    //Temporary stores messages
    public String msg = "";
    public int totalMessages = 0;
    public int printedMessages = 0;
    public boolean canIJoin;
    public Object cachedGUI;

    //Functions that needs to be run from cache.
    public void newTab(Client gui, String name) {
        JScrollBar jsb = new JScrollBar();
        JTextPane jtp = new JTextPane();

        jtp.setMinimumSize(gui.guiDisplay.getMinimumSize());
        jtp.setMaximumSize(gui.guiDisplay.getMaximumSize());
        jtp.setPreferredSize(gui.guiDisplay.getPreferredSize());
        jtp.setSize(gui.guiDisplay.getSize());
        jtp.setContentType(gui.guiDisplay.getContentType());
        jtp.setEditable(false);

        jsb.setAutoscrolls(true);
        jsb.add(jtp);
        gui.paneTabs.add(name, jtp);
        gui.listModel.addElement(name);
        gui.channelList.setSelectedIndex(gui.listModel.indexOf(name));

        ClientCache.getInstance().panes.add(jtp);
        System.out.println("Pane: "+name+" Added at:"+(ClientCache.getInstance().panes.size()));
    }
    public void removeTab(Client gui, String name){
        gui.paneTabs.remove(gui.paneTabs.indexOfTab(name));
        gui.listModel.removeElement(name);
        gui.channelList.setSelectedIndex(gui.listModel.indexOf(0));
        ClientCache.getInstance().panes.remove(name);
    }

    //Getters and setters for cached messaging objects and variables
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getChannelName() {
        return channelName;
    }
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
