package eu.dm2e.silk.wsmanager;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.container.grizzly2.GrizzlyWebContainerFactory;
import org.glassfish.grizzly.http.server.HttpServer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kai
 * Date: 5/27/13
 * Time: 9:01 AM
 * To change this template use File | Settings | File Templates.
 */
@Path("/manage")
public class ManageService {

    static HttpServer httpServer;
    static HttpServer manageServer;

    @GET
    @Path("/stop")
    public String stop() {
        if (httpServer!=null) httpServer.stop();
        // if (manageServer!=null) manageServer.stop();
        return "STOPPED";
    }


    public static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:9991/").build();
    }
    public static void startServer() {
        final Map<String, String> initParams = new HashMap<String, String>();
        final Map<String, String> initParams2 = new HashMap<String, String>();

        initParams.put("com.sun.jersey.config.property.packages", "eu.dm2e.silk.services");
        initParams2.put("com.sun.jersey.config.property.packages", "eu.dm2e.silk.wsmanager");

        System.out.println("Starting grizzly2...");
        try {
            manageServer = GrizzlyWebContainerFactory.create(UriBuilder.fromUri("http://localhost:9992/").build(), initParams2);
            httpServer =  GrizzlyWebContainerFactory.create(getBaseURI(), initParams);
        } catch (IOException e) {
            throw new RuntimeException("An exception occurred: " + e, e);
        }

    }


    public static void startAll() {
        startServer();
    }

    public static void stopAll() {

        Client client = new Client();
        String response = client.resource("http://localhost:9992/manage/stop").get(String.class);
        System.out.println("Stopped all servers. Last response: " + response);
    }
}
