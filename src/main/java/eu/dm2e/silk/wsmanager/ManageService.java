package eu.dm2e.silk.wsmanager;

import java.net.ServerSocket;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dm2e.ws.SerializablePojoListMessageBodyWriter;
import eu.dm2e.ws.SerializablePojoProvider;
import eu.dm2e.ws.grafeo.jaxrs.GrafeoMessageBodyWriter;
//import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 * This file was created within the DM2E project.
 * http://dm2e.eu
 * http://github.com/dm2e
 *
 * Author: Kai Eckert, Konstantin Baierer
 */
@Path("/manage")
public class ManageService {
	
	static Logger log = LoggerFactory.getLogger(ManageService.class.getName());
	
	private static final int MANAGE_SERVER_PORT = 9992;
	private static final int HTTP_SERVER_PORT = 9991;

    private static HttpServer httpServer;
	private static HttpServer manageServer;

    @GET
    @Path("/start")
    public String start() {
    	startAll();
    	return "Tried to start";
    }

    @GET
    @Path("/stop")
    public String stop() {
    	stopAll();
    	return "Tried to stop";
    }

    public static void startManageServer() {
        
        final ResourceConfig rc = new ResourceConfig().packages("eu.dm2e.ws.wsmanager");

        
        System.out.println("Starting manageServer");
        manageServer = GrizzlyHttpServerFactory.createHttpServer(UriBuilder.fromUri("http://localhost:" + MANAGE_SERVER_PORT + "/").build(), rc);

    }

    public static void startHttpServer() {
    	if (isPortInUse(HTTP_SERVER_PORT)) {
    		log.error(HTTP_SERVER_PORT + " is already in use");
    		return;
    	}
    	final ResourceConfig resourceConfig = new ResourceConfig()
        	.packages("eu.dm2e.silk.services")
        	// multipart/form-data
	        .register(MultiPartFeature.class)
	        // setting pojos as response entity
	        .register(SerializablePojoProvider.class)
	        // setting a list of pojos as response entity
	        .register(SerializablePojoListMessageBodyWriter.class)
	        // setting Grafeos as response entity
	        .register(GrafeoMessageBodyWriter.class)
	        // Log Jersey-internal server communication
	        .register(LoggingFilter.class);

        
        log.info("Starting httpServer");
        
        httpServer = GrizzlyHttpServerFactory.createHttpServer(
        		UriBuilder.fromUri("http://localhost:" + HTTP_SERVER_PORT + "/").build(), resourceConfig);
    }
    public static void stopHttpServer() {
        if (httpServer!=null) {
        	httpServer.stop();
        	if (isPortInUse(HTTP_SERVER_PORT)) {
        		log.error("Could not stop httpServer!");
        	}
        	httpServer = null;
        }
    }
    
    @GET
    @Path("/port/{port}")
    public static String isPortInUse(@PathParam("port") String portStr) {
    	int port = Integer.parseInt(portStr);
    	boolean resp = isPortInUse(port);
    	return Boolean.toString(resp);
    }
    
    public static boolean isPortInUse(int port) {
    	ServerSocket socket;
		try {
			socket = new ServerSocket(port);
			try {
				socket.close();
			} catch (Exception e) {
				log.trace("Deliberately do nothing.");
			}
		} catch (Exception e) {
			return true;
		}
		return false;
    }

    public static void startAll() {
    	
		if (null == manageServer) {
			startManageServer();
	    }
		if (null == httpServer) {
			startHttpServer();
	    }
    }

    public static void stopAll() {
    	
    	log.info("Stopping Http Server.");
    	stopHttpServer();
    }
}