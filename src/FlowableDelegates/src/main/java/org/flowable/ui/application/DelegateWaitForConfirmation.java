package org.flowable.ui.application;

import org.aas.enumeration.MessageType;
import org.aas.message.I4_0_message;
import org.aas.services.MsgParticipantServices;
import org.aas.services.UIServices;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;


@Service("DelegateWaitForConfirmation")
public class DelegateWaitForConfirmation implements JavaDelegate {

	private int refusalCounter = 0;
	private int confirmationCounter = 0;
	private int ingoingMessageCounter = 0;
	private int expectedMessageCounter = 0;
	private String selectedProposals = "";
	private I4_0_message readMessage_I40_messageObject = new I4_0_message();


	@Override
    public void execute(DelegateExecution execution) {

		String selectionStrategy = execution.getVariableInstance("proposalSelectionStrategy").getTextValue();

		//counting the incomming confirmations
		ingoingMessageCounter = execution.getVariable("ingoingMessageCounter",Integer.class);
		ingoingMessageCounter++;
		execution.setVariable("ingoingMessageCounter", ingoingMessageCounter);
		expectedMessageCounter = execution.getVariable("expectedMessageCounter",Integer.class);

		//initialize a new message object with the message template and fill in the values in the next step
		try {
			readMessage_I40_messageObject.deserializeMsg(execution.getVariable("msgTemplate", String.class));
			
		} catch (DeserializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		
		if(readMessage_I40_messageObject.type.getValue().compareTo(MessageType.confirming.toString()) == 0){
			confirmationCounter = execution.getVariable("confirmationCounter",Integer.class);
			confirmationCounter++;
			execution.setVariable("confirmationCounter", confirmationCounter);

			//Create string with confirmed proposals
			selectedProposals = execution.getVariable("form_selectedProposal",String.class);
			selectedProposals += UIServices.displayProposal(readMessage_I40_messageObject) + "\n\n"; 
			execution.setVariable("form_selectedProposal", selectedProposals);

		} else if (readMessage_I40_messageObject.type.getValue().compareTo(MessageType.refusal.toString()) == 0){
			refusalCounter = execution.getVariable("refusalCounter",Integer.class);
			refusalCounter++;
			execution.setVariable("refusalCounter", refusalCounter);
		}
		
		//set the delegate to an end when all expected proposals are collected
		if (expectedMessageCounter == ingoingMessageCounter){
			execution.setVariable("collectConfirmations", "end");
			execution.setVariable("form_selectionStrategy", selectionStrategy);
    	}

		execution.setVariable("form_status", "There are " + confirmationCounter + " of " + ingoingMessageCounter + " proposals confirmend and " + refusalCounter + " refused.");
				
    }
}