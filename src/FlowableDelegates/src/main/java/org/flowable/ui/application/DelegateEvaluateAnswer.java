package org.flowable.ui.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aas.enumeration.MessageType;
import org.aas.message.I4_0_message;
import org.aas.services.MsgParticipantServices;
import org.aas.services.SimpleServices;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;


@Service("DelegateEvaluateAnswer")
public class DelegateEvaluateAnswer implements JavaDelegate {
	
	@Override
    public void execute(DelegateExecution execution) {
		
        I4_0_message readAnswerMessage_I40_messageObject = MsgParticipantServices.getDefault_I40_MessageObject(DelegateReadMessage.I40_messageOperation);

		//complete message object with frame values
		readAnswerMessage_I40_messageObject.frameCollection = (DefaultSubmodelElementCollection) readAnswerMessage_I40_messageObject.ov_frame.getValue();
		readAnswerMessage_I40_messageObject = MsgParticipantServices.setFrameElements(readAnswerMessage_I40_messageObject, 
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
		String smcReferences = "";
		String smcDataElements = "";

		//Map flowable variables to I40_message object and
		//Prepare output for flowable UI
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
					smcReferences = smcReferences + "Reference: " + idShort +  " | Value: " + value + "\n";
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
					smcDataElements = smcDataElements + "Element: " + idShort +  " | Value: " + value + "\n";
					dataElement = SimpleServices.setDataElement(idShort, value);
					dataElementsList.add(dataElement);
				}
			}
			else{}
		}
		readAnswerMessage_I40_messageObject.submodelReferences.setValue(submodelReferencesList);
		readAnswerMessage_I40_messageObject.dataElements.setValue(dataElementsList);

		if (readAnswerMessage_I40_messageObject.type.getValue().toString().equals(MessageType.offer_acceptance.toString())){
            execution.setVariable("offerAccepted", "true");
			//Test for plausibility needs to be added
            execution.setVariable("answerPlausible", "true");
			execution.setVariable("resultBiddingProcess", "The answer was evaluated and the proposal accepted.");
        }
        else if (readAnswerMessage_I40_messageObject.type.getValue().toString().equals(MessageType.offer_rejection.toString())){
            execution.setVariable("offerAccepted", "false");
            execution.setVariable("resultBiddingProcess", "The answer was evaluated but the proposal not accepted.");   
        }

		execution.setVariable("form_interactionElements_SubmodelReferences", smcReferences);
		execution.setVariable("form_interactionElements_DataElements", smcDataElements);
		execution.setVariable("form_type", readAnswerMessage_I40_messageObject.type.getValue());
		execution.setVariable("form_sender", readAnswerMessage_I40_messageObject.sender.getValue());
		execution.setVariable("form_receiver", readAnswerMessage_I40_messageObject.receiver.getValue());
		execution.setVariable("form_conversationId", readAnswerMessage_I40_messageObject.conversationId.getValue());
		execution.setVariable("form_messageId", readAnswerMessage_I40_messageObject.messageId.getValue());
		execution.setVariable("form_replyTo", readAnswerMessage_I40_messageObject.replyTo.getValue());
		execution.setVariable("form_replyBy", readAnswerMessage_I40_messageObject.replyBy.getValue());
		execution.setVariable("form_semanticProtocol", readAnswerMessage_I40_messageObject.semanticProtocol.getValue());
		execution.setVariable("form_role", readAnswerMessage_I40_messageObject.role.getValue());

		String requestedAASID = execution.getVariableInstance("receiver").getTextValue();
		execution.setVariable("requestedAASID", requestedAASID);

		try {
			execution.setVariable("outgoingMessage", readAnswerMessage_I40_messageObject.serialize());
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}