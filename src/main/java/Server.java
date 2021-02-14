import com.sun.net.httpserver.HttpServer;

import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Properties;

public class Server {
    private HttpServer server;

    private int serverPort;
    private String decodedPath;
    private Routers routers;

    public HttpServer getServer() {
        return server;
    }

    private void getConfigFile() throws IOException {
        String path = Server.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        decodedPath = URLDecoder.decode(path, "UTF-8");
        decodedPath = decodedPath.replaceAll("simple-webapp-java.jar", "");

        Properties prop = new Properties();
        String propFileName = (new StringBuilder()).append(path).append("config.properties").toString();
        File file = new File(propFileName);
        InputStream inputStream = new FileInputStream(file);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        if (prop.getProperty("serverPort") == "") {
            serverPort = 3001;
        } else {
            serverPort = Integer.parseInt(prop.getProperty("serverPort"));
        }
    }

    public void bootstrap() throws IOException, URISyntaxException {
        getConfigFile();
        server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        routers = new Routers();
        routers.applyRouters(server);

        server.setExecutor(null); // creates a default executor
        server.start();

        System.out.format("Server started on %d\n", serverPort);

        StringBuilder url = new StringBuilder().append("http://localhost:").append(serverPort);

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(url.toString()));
        }
    }

    public void shutdown() {

    }
}
