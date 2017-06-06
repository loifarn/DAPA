package dapa;

import com.google.gson.Gson;
import dapa.managers.FileManager;
import dapa.server.ServerHost;

import java.io.*;
import java.nio.file.Files;

public class Runner {
    public static void main(String[] args) {
        Gson gson = new Gson();
        if (!new File("serverConfig.json").exists()) {
            if (FileManager.getInstance().writeToFile("serverConfig.json", gson.toJson(ServerConfig.getInstance()))) {
                System.out.println("Please fill out the config file that was generated and restart the server");
            }
        } else {
            String config = FileManager.getInstance().readFromFile("serverConfig.json");
            if (config != null) {
                ServerConfig.setInstance(gson.fromJson(config, ServerConfig.class));
                ServerHost host = new ServerHost();
                host.runServer();
            }
        }
    }
}
