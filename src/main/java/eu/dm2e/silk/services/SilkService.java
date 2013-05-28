package eu.dm2e.silk.services;

import de.fuberlin.wiwiss.silk.Silk;
import eu.dm2e.ws.api.FilePojo;
import eu.dm2e.ws.api.WebservicePojo;
import eu.dm2e.ws.grafeo.jena.GrafeoImpl;
import eu.dm2e.ws.services.AbstractTransformationService;
import eu.dm2e.ws.services.Client;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.ws.rs.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: dritze
 * Date: 5/23/13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
@Path("/silk")
public class SilkService extends AbstractTransformationService {

    public SilkService() {
        WebservicePojo ws = getWebServicePojo();
        ws.addInputParameter("config").setIsRequired(true);
        ws.addInputParameter("inputSource").setIsRequired(true);
        ws.addInputParameter("inputDestination").setIsRequired(true);
        ws.addOutputParameter("generatedLinks");
    }

    @Override
    public void run() {
        jobPojo.setStarted();
        GrafeoImpl g =null;

        try {
            File config = crateTempFile(new URL(jobPojo.getWebserviceConfig().getParameterValueByName("config")));
            File ds1 = crateTempFile(new URL(jobPojo.getWebserviceConfig().getParameterValueByName("inputSource")));
            File ds2 = crateTempFile(new URL(jobPojo.getWebserviceConfig().getParameterValueByName("inputDestination")));

            log.info("Config file stored: " + config.getAbsolutePath());
            log.info("Input Source file stored: " + ds1.getAbsolutePath());
            log.info("Input Destination file stored: " + ds2.getAbsolutePath());

            setDataSource(config, ds1, true);
            setDataSource(config, ds2, false);
            File file = File.createTempFile("silk_out",".xml");
            log.info("Output will be created in : " + file.getAbsolutePath());
            setOutput(config, file);
            log.info("Starting Silk...");
            Silk.executeFile(config, null, 1, true);

            /*
            BufferedReader read = new BufferedReader(new FileReader(file));
            String line = read.readLine();
            while(line !=null) {
                System.out.println(line);
                line = read.readLine();
            }
            read.close();
            */

            g = new GrafeoImpl(file);

            log.info("Result: " + g.getTurtle());

            Client client = new Client();
            String resulturl = client.publishFile(file, new FilePojo());
            jobPojo.addOutputParameterAssignment("generatedLinks", resulturl);
            config.deleteOnExit();
            ds1.deleteOnExit();
            ds2.deleteOnExit();
            file.deleteOnExit();
            jobPojo.setFinished();
            jobPojo.publish();
            log.info("Resulting Job Object: " + jobPojo.getTurtle());
        } catch (MalformedURLException e) {
            jobPojo.addLogEntry("Exception orccured: " + e,"ERROR");
            jobPojo.setFailed();
            jobPojo.publish();
        } catch (IOException e) {
            jobPojo.addLogEntry("Exception orccured: " + e,"ERROR");
            jobPojo.setFailed();
            jobPojo.publish();
        }
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
            file = File.createTempFile("silk-inputs",".xml");
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
