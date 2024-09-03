package org.flowable.ui.application;

import java.util.*;

import org.aas.http.api.NodeRedAPI;
import org.aas.message.I4_0_message;
import org.aas.services.SimpleServices;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReferenceElement;

@Service("DelegateDetermineFeasibleScope")
public class DelegateDetermineFeasibleScope implements JavaDelegate {

	private I4_0_message determineFeasibility_I40_messageObject = new I4_0_message();
		
	@Override
    public void execute(DelegateExecution execution) {

		//String requestedAASID = execution.getVariable("requestedAASID", String.class);
		boolean proposalFeasible = false; //Has to be changed to "false" when function is integrated

		try {
			determineFeasibility_I40_messageObject.deserializeMsg(execution.getVariable("incommingMessage", String.class));
                
            } catch (DeserializationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
		
		//Festlegen, welche zusätzlichen Variablen für den Algorithmus notwendig sind

		//invoke AAS operation "determineFeasibleScope" with input variables
		DefaultReferenceElement tmpReference = SimpleServices.findReferenceElementWithIdShort(determineFeasibility_I40_messageObject.submodelReferences.getValue(), "ProductTypeReference");
		String tmpProposal = NodeRedAPI.invokeDetermineFeasibleScope(determineFeasibility_I40_messageObject.receiver.getValue(), tmpReference.getValue().getKeys().get(0).getValue());

		//deserialize the proposal to a single AAS property in the dataElements collection
		DefaultProperty proposalDataElement = new DefaultProperty();
		proposalDataElement.setIdShort("DataElementProposal");
		proposalDataElement.setValue(tmpProposal);

		List<SubmodelElement> smcCopy = new ArrayList<>();
		smcCopy.add(proposalDataElement);
		determineFeasibility_I40_messageObject.dataElements.setValue(smcCopy);	

		List<SubmodelElement> smlTemp = new ArrayList<>();
		smlTemp.add(determineFeasibility_I40_messageObject.submodelReferences);
		smlTemp.add(determineFeasibility_I40_messageObject.dataElements);
		determineFeasibility_I40_messageObject.interactionElementsCollection.setValue(smlTemp);

		//formating output for user		
		ObjectMapper mapper = new ObjectMapper();
		String formattedProposal = "";
		try {
			JsonNode node = mapper.readTree(tmpProposal);
			formattedProposal = node.toPrettyString();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		execution.setVariable("form_Proposal", formattedProposal);
		
		if(formattedProposal.contains("\"success\" : true")){
			proposalFeasible = true;
		} else if (formattedProposal.contains("\"success\" : false")){
			proposalFeasible = false;
		}

		if (proposalFeasible) {
			execution.setVariable("message", "offer");
		} else{
			execution.setVariable("message", "refusal");
			execution.setVariable("resultBiddingProcess", "A refusal is sent.");
		}

		//set outgoing message variable in flowable
		try {
			execution.setVariable("outgoingMessage", determineFeasibility_I40_messageObject.serialize());
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
