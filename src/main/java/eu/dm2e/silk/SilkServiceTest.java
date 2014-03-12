package eu.dm2e.silk;



import eu.dm2e.silk.services.SilkService;
import eu.dm2e.ws.api.JobPojo;
import eu.dm2e.ws.api.WebserviceConfigPojo;
import eu.dm2e.ws.api.WebservicePojo;
import eu.dm2e.ws.services.Client;
import java.io.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class SilkServiceTest {

    public static void main(String args[]) throws IOException, InterruptedException {

        String base = "http://localhost:9998/api/";
        String silk = base + "service/silk";

        Client client = new Client();

        WebservicePojo silkPojo = new WebservicePojo();
        silkPojo.loadFromURI(silk);
        WebserviceConfigPojo conf = new WebserviceConfigPojo();

        conf.setWebservice(new SilkService().getWebServicePojo());
        conf.addParameterAssignment(SilkService.CONFIG, "https://dl.dropboxusercontent.com/u/10852027/dm2e/library.xml");
        conf.addParameterAssignment(SilkService.INPUT_SOURCE, "https://dl.dropboxusercontent.com/u/10852027/dm2e/302.rdf");
        conf.addParameterAssignment(SilkService.INPUT_DESTINATION, "https://dl.dropboxusercontent.com/u/10852027/dm2e/303.rdf");
        conf.publishToService(client.getConfigWebTarget());
        client.publishPojoToConfigService(conf);

        System.out.println("Configuration created for Test: " + conf.getTurtle());
        System.out.println(silkPojo.getId());

        Response response = client.target(silkPojo.getId()).request().put(Entity.text(conf.getId()));

        JobPojo job = new JobPojo();
        job.loadFromURI(response.getLocation());
        while (job.isStillRunning()) {
            Thread.sleep(2000);
            job.refresh(0, true);
            System.out.println(job.getJobStatus() + ": " + job.getLatestResult());
        }

    }
}
