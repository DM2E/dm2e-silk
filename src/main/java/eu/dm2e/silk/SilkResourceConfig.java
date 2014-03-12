/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.dm2e.silk;

import eu.dm2e.grafeo.jaxrs.GrafeoMessageBodyWriter;
import eu.dm2e.grafeo.json.GrafeoJsonSerializer;
import eu.dm2e.ws.SerializablePojoListMessageBodyWriter;
import eu.dm2e.ws.SerializablePojoProvider;
import eu.dm2e.ws.api.FilePojo;
import eu.dm2e.ws.api.JobPojo;
import eu.dm2e.ws.api.LogEntryPojo;
import eu.dm2e.ws.api.ParameterAssignmentPojo;
import eu.dm2e.ws.api.ParameterConnectorPojo;
import eu.dm2e.ws.api.ParameterPojo;
import eu.dm2e.ws.api.UserPojo;
import eu.dm2e.ws.api.VersionedDatasetPojo;
import eu.dm2e.ws.api.WebserviceConfigPojo;
import eu.dm2e.ws.api.WebservicePojo;
import eu.dm2e.ws.api.WorkflowPojo;
import eu.dm2e.ws.api.WorkflowPositionPojo;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author domi
 */
public class SilkResourceConfig extends ResourceConfig {
    public SilkResourceConfig () {
            packages("eu.dm2e.ws.services").packages("eu.dm2e.silk");
        	// multipart/form-data
	        register(MultiPartFeature.class);
	        // setting pojos as response entity
	        register(SerializablePojoProvider.class);
	        // setting a list of pojos as response entity
	        register(SerializablePojoListMessageBodyWriter.class);
	        // setting Grafeos as response entity
	        register(GrafeoMessageBodyWriter.class);
	        // Log Jersey-internal server communication
	        register(LoggingFilter.class);
                
                GrafeoJsonSerializer.registerType(JobPojo.class);
		 GrafeoJsonSerializer.registerType(FilePojo.class);
		 GrafeoJsonSerializer.registerType(LogEntryPojo.class);
		 GrafeoJsonSerializer.registerType(ParameterAssignmentPojo.class);
		 GrafeoJsonSerializer.registerType(ParameterConnectorPojo.class);
		 GrafeoJsonSerializer.registerType(ParameterPojo.class);
		 GrafeoJsonSerializer.registerType(UserPojo.class);
		 GrafeoJsonSerializer.registerType(VersionedDatasetPojo.class);
		 GrafeoJsonSerializer.registerType(WebserviceConfigPojo.class);
		 GrafeoJsonSerializer.registerType(WebservicePojo.class);
		 GrafeoJsonSerializer.registerType(WorkflowPojo.class);
		 GrafeoJsonSerializer.registerType(WorkflowPositionPojo.class);
    }
    
}
