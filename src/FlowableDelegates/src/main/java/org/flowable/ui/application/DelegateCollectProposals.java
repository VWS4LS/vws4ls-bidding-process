package org.flowable.ui.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aas.message.I4_0_message;
import org.aas.services.MsgParticipantServices;
import org.aas.services.SimpleServices;
import org.aas.services.UIServices;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;


@Service("DelegateCollectProposals")
public class DelegateCollectProposals implements JavaDelegate {


	@Override
    public void execute(DelegateExecution execution) {

		//counting the incomming proposals
		int proposalCounter = execution.getVariable("proposalCounter",Integer.class);
		proposalCounter++;
		execution.setVariable("proposalCounter", proposalCounter);

		//set the delegate to an end when all expected proposals are collected
		if (DelegateSendOutCallForProposal.receiverCount == proposalCounter){
			execution.setVariable("collectProposal", "end");
    	}
		String formVariable = "form_proposal" + proposalCounter;

		String messageType = execution.getVariableInstance("type").getTextValue();
		switch (messageType) {
			case "offer":
				I4_0_message readMessage_I40_messageObject = MsgParticipantServices.getDefault_I40_MessageObject(DelegateCreateCFP.I40_messageOperation);

				//complete message object with frame values
				readMessage_I40_messageObject.frameCollection = (DefaultSubmodelElementCollection) readMessage_I40_messageObject.ov_frame.getValue();
				readMessage_I40_messageObject = MsgParticipantServices.setFrameElements(readMessage_I40_messageObject, 
															execution.getVariableInstance("type").getTextValue(), 
															execution.getVariableInstance("sender").getTextValue(), 
															execution.getVariableInstance("receiver").getTextValue(), 
															execution.getVariableInstance("conversationId").getTextValue(),
															execution.getVariableInstance("messageId").getTextValue(),
															execution.getVariableInstance("replyTo").getTextValue(),
															execution.getVariableInstance("replyBy").getTextValue(),
															execution.getVariableInstance("semanticProtocol").getTextValue(), 
															execution.getVariableInstance("role").getTextValue());

				//Prepare output for flowable UI
				List<SubmodelElement> submodelReferencesList = new ArrayList<>();
				List<SubmodelElement> dataElementsList = new ArrayList<>();
				Set<String> variableNames = execution.getVariableNames();

				for (String name : variableNames){		
					String idShort = "";
					String value = "";
					if (name.contains("submodelReference_")){
						idShort = execution.getVariableInstance(name).getName().replace("submodelReference_", "");
						value = execution.getVariableInstance(name).getTextValue();
						if(value.compareTo("")==0){
							//delete local varibale in flowable because it wont be used in the later process steps
							execution.removeVariable(name);
						} else{
							DefaultReferenceElement submodelReference = new DefaultReferenceElement();
							submodelReference = SimpleServices.setReferenceElement(idShort, value);
							submodelReferencesList.add(submodelReference);
						}
					}
					else if(name.contains("dataElement_")){
						idShort = execution.getVariableInstance(name).getName().replace("dataElement_", "");
						value = execution.getVariableInstance(name).getTextValue();
						if(value.compareTo("")==0){
							//delete local varibale in flowable because it wont be used in the later process steps
							execution.removeVariable(name);
						} else{
							DefaultProperty dataElement = new DefaultProperty();
							dataElement = SimpleServices.setDataElement(idShort, value);
							dataElementsList.add(dataElement);
						}
					}
					else{}
				}
				readMessage_I40_messageObject.submodelReferences.setValue(submodelReferencesList);
				readMessage_I40_messageObject.dataElements.setValue(dataElementsList);

				System.out.println("Collect InteractionCollection: " + readMessage_I40_messageObject.interactionElementsCollection.toString());

				execution.setVariable(formVariable, UIServices.displayProposal(readMessage_I40_messageObject));

				try {
					String tmpProposals = execution.getVariable("allCollectedProposals", String.class);
					tmpProposals = SimpleServices.serializeCollectedObjects(readMessage_I40_messageObject.serialize(), tmpProposals);
					execution.setVariable("allCollectedProposals", tmpProposals);
				} catch (SerializationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case "refusal":
				execution.setVariable(formVariable, "Service provider refused call for proposal.");
				break;

			case "not understood":
				execution.setVariable(formVariable, "Service provider did not understand call for proposal.");
				break;
		
			default:
				break;
		}
	}
}