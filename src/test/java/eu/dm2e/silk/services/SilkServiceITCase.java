package eu.dm2e.silk.services;

import static org.junit.Assert.*;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dm2e.silk.wsmanager.ManageService;
import eu.dm2e.ws.api.JobPojo;
import eu.dm2e.ws.api.WebserviceConfigPojo;
import eu.dm2e.ws.api.WebservicePojo;
import eu.dm2e.ws.grafeo.Grafeo;
import eu.dm2e.ws.grafeo.jena.GrafeoImpl;
import eu.dm2e.ws.model.JobStatus;


/**
 * Created with IntelliJ IDEA.
 * User: kai
 * Date: 5/28/13
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SilkServiceITCase  {

    Logger log = LoggerFactory.getLogger(getClass());

    String SERVICE_URI = "http://localhost:9991/silk";

    @Before
    public void setUp() throws Exception {
        ManageService.startAll();
        eu.dm2e.ws.wsmanager.ManageService.startAll();
    }

    @Test
    public void testSilk() {


        /**
         * SET UP THE CONFIGURATION
         */
        Client client = ClientBuilder.newClient();
        WebTarget webResource = client.target(SERVICE_URI);
        WebserviceConfigPojo config = new WebserviceConfigPojo();
        WebservicePojo ws = new WebservicePojo();
        Grafeo gtest = new GrafeoImpl();
        gtest.load(SERVICE_URI);
        log.info("Triples actually found: " + gtest.getTurtle());
        ws.loadFromURI(SERVICE_URI);
        WebservicePojo ws2 = gtest.getObjectMapper().getObject(WebservicePojo.class, SERVICE_URI);
        log.info("Webservice found: " + ws.getTurtle());
        log.info("Webservice manually loaded: : " + ws2.getTurtle());
        config.setWebservice(ws);
		config.getParameterAssignments().add(ws
			.getParamByName("inputSource")
			.createAssignment("https://dl.dropboxusercontent.com/u/10852027/dm2e/302.rdf"));
		config.getParameterAssignments().add(ws
			.getParamByName("inputDestination")
			.createAssignment("https://dl.dropboxusercontent.com/u/10852027/dm2e/303.rdf"));
		config.getParameterAssignments().add(ws
			.getParamByName("config")
			.createAssignment("https://dl.dropboxusercontent.com/u/10852027/dm2e/library.xml"));

        /**
         * TRIGGER SILK SERVICE
         */
        log.info("Configuration created for Test: " + config.getTurtle());
        // ClientResponse response = webResource.post(ClientResponse.class, config.getTurtle());
        //log.info("JOB STARTED WITH RESPONSE: " + response.getStatus() + " / Location: " + response.getLocation() + " / Content: " + response.getEntity(String.class));
        // URI joburi = response.getLocation();
        eu.dm2e.ws.services.Client dm2eClient = new eu.dm2e.ws.services.Client();

        log.warn("" + dm2eClient.getConfigWebTarget().getUri());
        config.publishToService(dm2eClient.getConfigWebTarget());

        Response response = client
                .target(SERVICE_URI)
                .request()
                .put(Entity.text(config.getId()));
        log.info("JOB STARTED WITH RESPONSE: " + response.getStatus() + " / Location: " + response.getLocation() + " / Content: " + response.readEntity(String.class));
        assertEquals(202, response.getStatus());
        URI joburi = response.getLocation();

        /**
         * WAIT FOR JOB TO BE FINISHED
         */
        String status = JobStatus.NOT_STARTED.name();
        JobPojo job = null;
        while (status.equals(JobStatus.NOT_STARTED.name()) || status.equals(JobStatus.STARTED.name())) {
            Grafeo g = new GrafeoImpl(joburi.toString());
            job = g.getObjectMapper().getObject(JobPojo.class, joburi.toString());
            status =  job.getJobStatus();
            log.info("Check for status: " + status);
            log.info("JOB SO FAR: " + job.getTurtle());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException("An exception occurred: " + e, e);
            }
        }

        /**
         * CHECK IF JOB IS FINISHED
         */
        log.info("Status: " + status);
        assert(status.equals(JobStatus.FINISHED.name()));
        String url = job.getOutputParameterValueByName("generatedLinks");
        log.info("Results are at: " + url);
        Grafeo g = new GrafeoImpl();
        g.load(url);
        log.info("Generated Links: " + g.getTurtle());
    }

}
