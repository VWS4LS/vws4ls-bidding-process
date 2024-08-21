package org.aas.http.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.aas.message.I4_0_message;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

    public static String invokeSelectBestProposals(I4_0_message[] I40message, String selectionStrategy){
        
        List<ObjectNode> JSONInputBody = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        
        for (I4_0_message msg : I40message) {
            ObjectNode json = mapper.createObjectNode();
            DefaultProperty prop = (DefaultProperty) msg.dataElements.getValue().get(0);
            String sender = msg.sender.getValue();
            String conversationId = msg.conversationId.getValue();
            String proposal = prop.getValue();

            ObjectNode node = mapper.createObjectNode();
            try {
                node = mapper.readValue(proposal, ObjectNode.class);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            json.put("Sender", sender);
            json.put("ConversationId", conversationId);
            json.set("Proposal", node);

            JSONInputBody.add(json);
        }
        
        String URLString = NODE_RED_URL + "/aasCommunicationManager/selectBestProposals?selectionStrategy=" + selectionStrategy;
        String response = new String();
        System.out.println("invoked Path: " + URLString);

        try {

            URL url = new URL(URLString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

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