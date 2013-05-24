package eu.dm2e.silk.services;

import com.hp.hpl.jena.vocabulary.RDF;
import de.fuberlin.wiwiss.silk.Silk;
import eu.dm2e.ws.grafeo.jena.GrafeoImpl;
import eu.dm2e.ws.services.AbstractRDFService;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;

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
        GrafeoImpl g =null;

        try {
            File config = crateTempFile(new URL("https://dl.dropboxusercontent.com/u/10852027/dm2e/library.xml"));
            File ds1 = crateTempFile(new URL("https://dl.dropboxusercontent.com/u/10852027/dm2e/302.rdf"));
            File ds2 = crateTempFile(new URL("https://dl.dropboxusercontent.com/u/10852027/dm2e/303.rdf"));
            setDataSource(config, ds1, true);
            setDataSource(config, ds2, false);
            File file = File.createTempFile("silk_out",".xml");
            setOutput(config, file);
            Silk.executeFile(config, null, 1, true);

            BufferedReader read = new BufferedReader(new FileReader(file));
            String line = read.readLine();
            while(line !=null) {
                System.out.println(line);
                line = read.readLine();
            }
            read.close();

            g = new GrafeoImpl(file);

            config.deleteOnExit();
            ds1.deleteOnExit();
            ds2.deleteOnExit();
            file.deleteOnExit();

        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return getResponse(g);
    }

    private void setDataSource(File config, File datasource, boolean first) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(config);

            Node n;
            if(first) {
                n = doc.getElementsByTagName("DataSource").item(0);
            }
            else {
                n = doc.getElementsByTagName("DataSource").item(1);
            }

            NodeList children = n.getChildNodes();
            for(int i=0; i<children.getLength(); i++) {
                Node child = children.item(i);
                NamedNodeMap atts = child.getAttributes();
                if(atts ==null) {
                    continue;
                }
                    if(atts.item(0).getNodeName().equals("name") && atts.item(0).getTextContent().equals("file")) {
                        atts.item(1).setNodeValue(datasource.getAbsolutePath());
                }

                FileOutputStream fos = new FileOutputStream(config);
                DOMImplementationRegistry reg = DOMImplementationRegistry.newInstance();
                DOMImplementationLS impl = (DOMImplementationLS) reg.getDOMImplementation("LS");
                LSSerializer serializer = impl.createLSSerializer();
                LSOutput lso = impl.createLSOutput();
                lso.setByteStream(fos);
                serializer.write(doc,lso);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    private void setOutput(File config, File output) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(config);

            Node n = doc.getElementsByTagName("Output").item(0);

            NodeList children = n.getChildNodes();
            for(int i=0; i<children.getLength(); i++) {
                Node child = children.item(i);
                NamedNodeMap atts = child.getAttributes();
                if(atts ==null) {
                    continue;
                }
                if(atts.item(0).getNodeName().equals("name") && atts.item(0).getTextContent().equals("file")) {
                    atts.item(1).setNodeValue(output.getAbsolutePath());
                }

                FileOutputStream fos = new FileOutputStream(config);
                DOMImplementationRegistry reg = DOMImplementationRegistry.newInstance();
                DOMImplementationLS impl = (DOMImplementationLS) reg.getDOMImplementation("LS");
                LSSerializer serializer = impl.createLSSerializer();
                LSOutput lso = impl.createLSOutput();
                lso.setByteStream(fos);
                serializer.write(doc,lso);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private File crateTempFile(URL url) {
        File file = null;
        try {
            file = File.createTempFile("silk_config",".xml");
            URLConnection connect = url.openConnection();
            FileWriter writer = new FileWriter(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            String line = reader.readLine();
            while (line!=null) {
                writer.write(line+"\n");
                line = reader.readLine();
            }
            reader.close();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return file;
    }

}
