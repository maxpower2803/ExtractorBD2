package app.db;

import app.models.ServerInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Reads servers.properties external file from working directory (./servers.properties)
 */
public final class ServerConfig {

    private static final String DEFAULT_PATH = "servers.properties";

    public static List<ServerInfo> loadFromDefaultPath() throws IOException {
        return loadFromPath(Path.of(DEFAULT_PATH));
    }

    public static List<ServerInfo> loadFromPath(Path path) throws IOException {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(path.toFile())) {
            props.load(in);
        }
        List<ServerInfo> servers = new ArrayList<>();
        for (String name : props.stringPropertyNames()) {
            if (name.endsWith(".name")) {
                String id = name.substring(0, name.indexOf('.'));
                String displayName = props.getProperty(id + ".name", id);
                String host = props.getProperty(id + ".host", "localhost");
                int port = Integer.parseInt(props.getProperty(id + ".port", "1433"));
                String database = props.getProperty(id + ".database", "master");
                String user = props.getProperty(id + ".user", "");
                String password = props.getProperty(id + ".password", "");
                servers.add(new ServerInfo(id, displayName, host, port, database, user, password));
            }
        }
        return servers;
    }
}
