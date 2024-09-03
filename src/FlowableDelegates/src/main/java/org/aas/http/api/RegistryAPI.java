package org.aas.http.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelDescriptor;

public class RegistryAPI {

    private static JsonDeserializer deserializer = new JsonDeserializer();

    public static List<DefaultAssetAdministrationShellDescriptor> getAllShellDescriptors(String registryEndpointAsString){
        List<DefaultAssetAdministrationShellDescriptor> shellDescriptors = new ArrayList<>();
        String urlString = registryEndpointAsString + "/shell-descriptors";
        String response = new String();

        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		    }

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output = null;
        while ((output = br.readLine()) != null) {
            response += output;
		}
        //cleaning respsonse for deserialization
        response = response.replace("{\"paging_metadata\":{\"cursor\":null},\"result\":", "");
        response = response.substring(0, response.lastIndexOf("}"));

		conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        //deserialize Response to AAS Descriptors
        try {
            shellDescriptors = deserializer.readList(response, DefaultAssetAdministrationShellDescriptor.class);
            
        } catch (DeserializationException e) {
            e.printStackTrace();
        }	

        return shellDescriptors;     
	
    }

    public static DefaultAssetAdministrationShellDescriptor getAASDescriptorByAASID(String registryEndpointAsString, String aasIdentifierAsString){
        DefaultAssetAdministrationShellDescriptor shellDescriptor = new DefaultAssetAdministrationShellDescriptor();
        String encodedIdentifier = Base64.getEncoder().encodeToString(aasIdentifierAsString.getBytes());
        String urlString = registryEndpointAsString + "/shell-descriptors/" + encodedIdentifier;       
        String response = new String();

        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		    }

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output = null;
        while ((output = br.readLine()) != null) {
            response += output;
		}

		conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        //deserialize Response to AAS Descriptors
        try {
            shellDescriptor = deserializer.read(response, DefaultAssetAdministrationShellDescriptor.class);
            
        } catch (DeserializationException e) {
            e.printStackTrace();
        }	

        return shellDescriptor;     
	
    }

    public static DefaultSubmodelDescriptor getSMDescriptorBySMID(String registryEndpointAsString, String smIdentifierAsString){
        DefaultSubmodelDescriptor submodelDescriptor = new DefaultSubmodelDescriptor();
        String encodedIdentifier = Base64.getEncoder().encodeToString(smIdentifierAsString.getBytes());
        String urlString = registryEndpointAsString + "/submodel-descriptors/" + encodedIdentifier;     
        String response = new String();

        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		    }

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output = null;
        while ((output = br.readLine()) != null) {
            response += output;
		}

		conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        //deserialize Response to AAS Descriptors
        try {
            submodelDescriptor = deserializer.read(response, DefaultSubmodelDescriptor.class);
            
        } catch (DeserializationException e) {
            e.printStackTrace();
        }	

        return submodelDescriptor;     
	
    }

    
    
}
