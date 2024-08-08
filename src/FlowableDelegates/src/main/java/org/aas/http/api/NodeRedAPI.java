package org.aas.http.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.aas.message.I4_0_message;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NodeRedAPI{

    private static final String NODE_RED_URL = System.getenv().get("NODE_RED_URL");

    public static String invokeDetermineFeasibleScope(String machineAASID, String productTypeAASID){
        
        String[][] msg = {{"Machine_AAS_ID", machineAASID}, {"Product_Type_AAS_ID", productTypeAASID}};
        String URLString = NODE_RED_URL + "/aasCommunicationManager/determineFeasibleScope";
        String response = new String();
        System.out.println("invoked Path: " + URLString);

        try {

            URL url = new URL(URLString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            
            ObjectMapper mapper = new ObjectMapper();
            String input = mapper.writeValueAsString(msg);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            //When this isn't commented, than the invocation fails
            /* if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
            } */

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output = null;
            while ((output = br.readLine()) != null) {
                response += output;
            }
            System.out.println("Response of Node-RED invokeOperation: " + response);

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return response;
    }

    public static String invokeSelectBestProposals(I4_0_message[] JSONInputBody, String selectionStrategy){
        
        String URLString = NODE_RED_URL + "/aasCommunicationManager/selectBestProposals?selectionStrategy=" + selectionStrategy;
        String response = new String();
        System.out.println("invoked Path: " + URLString);

        try {

            URL url = new URL(URLString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            ObjectMapper mapper = new ObjectMapper();
            String input = mapper.writeValueAsString(JSONInputBody);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            //When this isn't commented, than the invocation fails
            /*if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
            } */

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output = null;
            while ((output = br.readLine()) != null) {
                response += output;
            }
            System.out.println("Response of Node-RED invokeOperation: " + response);

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return response;
    }
}