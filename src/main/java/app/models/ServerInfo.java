package app.models;

/**
 * Immutable model representing server configuration loaded from properties.
 */
public final class ServerInfo {
    private final String id;
    private final String name;
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

    public ServerInfo(String id, String name, String host, int port, String database, String user, String password) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUser() { return user; }
    public String getPassword() { return password; }

    @Override
    public String toString() { return name + " (" + host + ":" + port + ")"; }
}
