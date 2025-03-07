package org.aas.http.api;

import java.util.Base64;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;


public class RepositoryAPI {

    private static JsonDeserializer deserializer = new JsonDeserializer();
    private static final String AAS_REPO_USER_NAME = System.getenv().get("AAS_REPO_USER_NAME");
    private static final String AAS_REPO_PASSWORD = System.getenv().get("AAS_REPO_PASSWORD");

    public static DefaultAssetAdministrationShell getAASbyEndpoint(String aasEndpointAsString){
        DefaultAssetAdministrationShell aas = new DefaultAssetAdministrationShell();
        String response = new String();

        try{
            URI uri = new URI(aasEndpointAsString);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            String username = AAS_REPO_USER_NAME;
            String password = AAS_REPO_PASSWORD;
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeaderValue = "Basic " + encodedAuth;

            conn.setRequestProperty("Authorization", authHeaderValue);

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

        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //deserialize Response to AAS
        try {
            aas = deserializer.read(response, DefaultAssetAdministrationShell.class);
            
        } catch (DeserializationException e) {
            e.printStackTrace();
        }	

        return aas;     
	
    }

    public static SubmodelElementList getSubmodelElementList(String smEndpointAsString, String idShortPathAsString){
        SubmodelElementList sml = new DefaultSubmodelElementList();
        String encodedIdShortPath = URLEncoder.encode(idShortPathAsString,StandardCharsets.UTF_8);
        String urlEncoded = smEndpointAsString + "/submodel-elements/" + encodedIdShortPath;
        String response = new String();

        try{
            URI uri = new URI(urlEncoded);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            String username = AAS_REPO_USER_NAME;
            String password = AAS_REPO_PASSWORD;
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeaderValue = "Basic " + encodedAuth;

            conn.setRequestProperty("Authorization", authHeaderValue);

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

        } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

        //deserialize Response to AAS Descriptors
        try {
            sml = deserializer.read(response, DefaultSubmodelElementList.class);
            
        } catch (DeserializationException e) {
            e.printStackTrace();
        }	

		return sml;
	}

    public static Operation getOperation(String smEndpointAsString, String idShortPathAsString){
        Operation op = new DefaultOperation();
        String encodedIdShortPath = URLEncoder.encode(idShortPathAsString,StandardCharsets.UTF_8);
        String urlEncoded = smEndpointAsString + "/submodel-elements/" + encodedIdShortPath;
        String response = new String();

        try{
            URI uri = new URI(urlEncoded);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            String username = AAS_REPO_USER_NAME;
            String password = AAS_REPO_PASSWORD;
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeaderValue = "Basic " + encodedAuth;

            conn.setRequestProperty("Authorization", authHeaderValue);

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

        } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

        //deserialize Response to AAS Descriptors
        try {
            op = deserializer.read(response, DefaultOperation.class);
            
        } catch (DeserializationException e) {
            e.printStackTrace();
        }	

		return op;
	}

   
    public static Property getSubmodelProperty(String smEndpointAsString, String idShortPathAsString){
        Property prop = new DefaultProperty();
        String encodedIdShortPath = URLEncoder.encode(idShortPathAsString,StandardCharsets.UTF_8);
        String urlEncoded = smEndpointAsString + "/submodel-elements/" + encodedIdShortPath;
        String response = new String();

        try{
            URI uri = new URI(urlEncoded);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            String username = AAS_REPO_USER_NAME;
            String password = AAS_REPO_PASSWORD;
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeaderValue = "Basic " + encodedAuth;

            conn.setRequestProperty("Authorization", authHeaderValue);

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

        } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

        //deserialize Response to AAS Descriptors
        try {
            prop = deserializer.read(response, DefaultProperty.class);
            
        } catch (DeserializationException e) {
            e.printStackTrace();
        }	

		return prop;
	}

    public static DefaultReferenceElement getSubmodelReferenceElement(String smEndpointAsString, String idShortPathAsString){
        DefaultReferenceElement ref = new DefaultReferenceElement();
        String encodedIdShortPath = URLEncoder.encode(idShortPathAsString,StandardCharsets.UTF_8);
        String urlEncoded = smEndpointAsString + "/submodel-elements/" + encodedIdShortPath;
        String response = new String();

        try{
            URI uri = new URI(urlEncoded);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            String username = AAS_REPO_USER_NAME;
            String password = AAS_REPO_PASSWORD;
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeaderValue = "Basic " + encodedAuth;

            conn.setRequestProperty("Authorization", authHeaderValue);

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

        } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

        //deserialize Response to AAS Descriptors
        try {
            ref = deserializer.read(response, DefaultReferenceElement.class);
            
        } catch (DeserializationException e) {
            e.printStackTrace();
        }	

		return ref;
	}

    public static void invokeOperation(String smEndpointAsString, String idShortPathAsString, String inputArgumentsAsJSONString){
        
        String encodedIdShortPath = URLEncoder.encode(idShortPathAsString,StandardCharsets.UTF_8);
        String urlEncoded = smEndpointAsString + "/submodel-elements/" + encodedIdShortPath +"/invoke";
        String response = new String();

        try {
            URI uri = new URI(urlEncoded);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String username = AAS_REPO_USER_NAME;
            String password = AAS_REPO_PASSWORD;
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeaderValue = "Basic " + encodedAuth;

            conn.setRequestProperty("Authorization", authHeaderValue);

            String input =inputArgumentsAsJSONString;

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

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

        } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
    }
    
}
