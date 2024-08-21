package org.aas.services;

import java.util.ArrayList;

import org.aas.message.I4_0_message;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReferenceElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UIServices {
        public static String getSMReferences(SubmodelElementCollection SubmodelElementCollection){
            String smcReferences = "";
            DefaultReferenceElement ref = new DefaultReferenceElement();

            for(int i = 0; i < SubmodelElementCollection.getValue().size(); i++){
                ref = (DefaultReferenceElement) SubmodelElementCollection.getValue().get(i);
                smcReferences = smcReferences + "Reference: " + ref.getIdShort() +  " | Value: " + ref.getValue().getKeys().get(0).getValue() + "\n";
            }

            return smcReferences;
        };

        public static String getSMDataElements(SubmodelElementCollection SubmodelElementCollection){
            String smcDataElements = "";
            DefaultProperty prop = new DefaultProperty();

            for(int i = 0; i < SubmodelElementCollection.getValue().size(); i++){
                prop = (DefaultProperty) SubmodelElementCollection.getValue().get(i);
                smcDataElements = smcDataElements + "Data Element: " + prop.getIdShort() + " | Value: " + prop.getValue() + "\n";
            }

            return smcDataElements;
        };

        public static String getReceiverInformation(ArrayList<String> receiverEndpointAsArray){
            String receiver = "";
            String receiverAASID = receiverEndpointAsArray.get(0);
            String receiverSubmodellID = receiverEndpointAsArray.get(2);

            receiver = "AAS-ID: " + receiverAASID + "\nSM-ID: " + receiverSubmodellID;

            return receiver;

        };

        public static String displayProposal(I4_0_message I40message){
            String proposal = "";
            //is only applicable if one data element of type proposal exists
            DefaultProperty prop = (DefaultProperty) I40message.dataElements.getValue().get(0);
            String jsonProposal = prop.getValue();
            ObjectMapper mapper = new ObjectMapper();
            String formattedProposal = "";

            proposal += "Sender: " + I40message.sender.getValue() + "\n";
            proposal += "ConversationId (ProposalId): " + I40message.conversationId.getValue() + "\n";

            //formatting output for user		
            try {
                JsonNode node = mapper.readTree(jsonProposal);
                formattedProposal = node.toPrettyString();
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            proposal += "Proposal: " + "\n";
            proposal += formattedProposal;
            
            return proposal;
        }
}
