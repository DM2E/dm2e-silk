package eu.dm2e.silk.services;

import de.fuberlin.wiwiss.silk.Silk;
import de.fuberlin.wiwiss.silk.config.LinkSpecification;
import de.fuberlin.wiwiss.silk.config.LinkingConfig;
import eu.dm2e.ws.grafeo.jena.GrafeoImpl;
import eu.dm2e.ws.services.AbstractRDFService;
import sun.org.mozilla.javascript.internal.ScriptRuntime;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.rmi.server.ObjID;

/**
 * Created with IntelliJ IDEA.
 * User: dritze
 * Date: 5/23/13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
@Path("/silk")
public class SilkService extends AbstractRDFService {

    @Path("/exec")
    @GET
    public Response getMapTest(@Context UriInfo uriInfo){

        try {
            URL url = new URL("https://dl.dropboxusercontent.com/u/10852027/dm2e/library.xml");
            URLConnection connect = url.openConnection();
            File file = File.createTempFile("silk_config",".xml");
            File file1 = File.createTempFile("silk_input1",".rdf");
            File file2 = File.createTempFile("silk_input2",".rdf");
            //file.deleteOnExit();

            FileWriter writer = new FileWriter(file);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            String line = reader.readLine();

            int datasource = 0;
            boolean source =false;
            String input1="";

            while (line!=null) {
                source=false;
                if(line.contains("<DataSource ")) {
                    datasource++;
                }
                if(line.contains("name=\"file\" value=")) {
                    int index = line.indexOf("value=")+7;
                    input1 = line.substring(index, line.indexOf("/>")-2);
                    input1 = input1.trim();
                    if(datasource==1) {
                        URL url1 = new URL(input1);

                        URLConnection connect1 = url1.openConnection();
                        BufferedReader reader1 = new BufferedReader(new InputStreamReader(connect1.getInputStream()));
                        FileWriter writer1 = new FileWriter(file1);
                        String line1 = reader1.readLine();
                        while(line1!=null) {
                             writer1.write(line1+"\n");
                            line1=reader1.readLine();
                        }
                        reader1.close();
                        writer1.flush();
                        writer1.close();
                        source = true;
                    }
                    if(datasource==2) {
                        URL url1 = new URL(input1);
                        URLConnection connect2 = url1.openConnection();
                        BufferedReader reader2 = new BufferedReader(new InputStreamReader(connect2.getInputStream()));
                        FileWriter writer2 = new FileWriter(file2);
                        String line2 = reader2.readLine();
                        while(line2!=null) {
                            writer2.write(line2+"\n");
                            line2=reader2.readLine();
                        }
                        reader2.close();
                        writer2.flush();
                        writer2.close();
                        source = true;
                    }

                }
                if(datasource==1 && source) {
                    writer.write("<Param name=\"file\" value=\""+file1.getAbsolutePath()+"\" />\n");
                    line = reader.readLine();
                    continue;
                }
                if(datasource==2 && source) {
                    writer.write("<Param name=\"file\" value=\""+file2.getAbsolutePath()+"\" />\n");
                    line = reader.readLine();
                    datasource=0;
                    continue;
                }
                else {
                    writer.write(line+"\n");
                }
                line = reader.readLine();
            }

            reader.close();
            writer.flush();
            writer.close();
            Silk.executeFile(file, null, 1, true);

        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return getResponse(new GrafeoImpl());
    }

}
