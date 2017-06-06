package dapa.server;

import dapa.ServerConfig;
import org.glassfish.tyrus.server.Server;
import dapa.server.endpoints.PublicEndpoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Timer;

public class ServerHost {
    public void runServer() {
        Server server = new Server(ServerConfig.getInstance().getHost(), ServerConfig.getInstance().getPort(), "", null, PublicEndpoint.class);
        Timer authChecker = new Timer();
        Timer heartBeat = new Timer();
        authChecker.scheduleAtFixedRate(new AuthCheckerTask(), new Date(new Date().getTime() + 10000), 10000);
        heartBeat.scheduleAtFixedRate(new HeartbeatTask(), new Date(new Date().getTime() + 50000), 50000);
        try {
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please press a key to stop the server.");
            reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
