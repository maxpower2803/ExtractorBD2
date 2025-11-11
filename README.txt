ExtractorBD-final (JavaFX + HikariCP) - External servers.properties (relative path)

How to use:
1. Place the compiled jar (or run via Maven) in a folder.
2. Create an external file called 'servers.properties' in the same folder as the jar.
   - You can use the provided 'servers.properties.example' as a starting point.
3. Run via Maven:
   mvn clean javafx:run
   or build jar and run:
   mvn clean package
   java -jar target/ExtractorBD-final-1.0-SNAPSHOT.jar

Notes:
- The app reads './servers.properties' (relative path) at startup.
- When you click a server, a HikariCP pool is created for that server (only once).
- Then databases are loaded; clicking a database loads its tables.
- For development the connection URL sets 'trustServerCertificate=true' to avoid SSL issues.
  In production, install a valid certificate and remove that flag.

Java version: 25
JavaFX version: 25
