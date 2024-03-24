package ui;

import server.ServerFacade;

public class Client {
    private final String serverUrl;
    private final ServerFacade server;
    public Client(String serverUrl) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
    }
}
