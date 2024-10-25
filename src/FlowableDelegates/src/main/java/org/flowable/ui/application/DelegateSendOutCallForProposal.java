package org.flowable.ui.application;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.aas.message.I4_0_message;
import org.aas.services.*;

import java.util.*;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service("DelegateSendOutCallForProposal")
public class DelegateSendOutCallForProposal implements JavaDelegate {

	//AAS components interface variables
	private final String AAS_REGISTRYPATH = System.getenv().get("AAS_REGISTRY_URL");
	private final String SM_REGISTRYPATH = System.getenv().get("SM_REGISTRY_URL");	
	private int receiverCount = 0;
	
	@Override
    public void execute(DelegateExecution execution) {

		I4_0_message I40_messageObject = new I4_0_message();
		List<String> aasEndpointList = new ArrayList<>();
		List<ArrayList<String>> receiverEndpoints = new ArrayList<>();

		//initialize a new message object with the message template and fill in the values in the next step
		try {
			I40_messageObject.deserializeMsg(execution.getVariable("outgoingMessage", String.class));
                
            } catch (DeserializationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
		
		aasEndpointList = DescriptorServices.getAASEndpointList(AAS_REGISTRYPATH);
		receiverEndpoints = MsgParticipantServices.getProtocolSpecificReceiverEndpoints(aasEndpointList, SM_REGISTRYPATH, "ServiceProvider", "VDI_2193-2");

		//Display found receivers for user ans send operation to service providers
		receiverCount = receiverEndpoints.size();
		execution.setVariable("receiverCounter", receiverCount);
		
		for (int i = 1; i <= receiverCount; i++) {
			String receiverAdressInformation = UIServices.getReceiverInformation(receiverEndpoints.get(i-1));
			String receiverEndpoint = receiverEndpoints.get(i-1).get(0);
			//fill in the receiver
			I40_messageObject = MsgParticipantServices.setFrameElements(I40_messageObject, receiverEndpoint);
			
			execution.setVariable("form_receiverAdress_" +i, receiverAdressInformation);

			//send Operation
			ArrayList<OperationVariable> inputArguments = new ArrayList<>();
			I40_messageObject.ov_frame.setValue(I40_messageObject.frameCollection);
			I40_messageObject.ov_interactionElements.setValue(I40_messageObject.interactionElementsCollection);
			inputArguments.add(I40_messageObject.ov_interactionElements);
			inputArguments.add(I40_messageObject.ov_frame);
			OperationServices.invokeOperation(receiverEndpoints.get(i-1).get(3), "newMessage", inputArguments);
		} 

	}
	

	

}


