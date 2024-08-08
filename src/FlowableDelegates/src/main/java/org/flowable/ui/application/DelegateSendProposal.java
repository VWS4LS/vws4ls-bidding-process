package org.flowable.ui.application;

import java.util.ArrayList;
import java.util.List;

import org.aas.enumeration.MessageType;
import org.aas.enumeration.SemanticProtocol;
import org.aas.http.api.RegistryAPI;
import org.aas.message.I4_0_message;
import org.aas.services.DescriptorServices;
import org.aas.services.MsgParticipantServices;
import org.aas.services.OperationServices;
import org.aas.services.UIServices;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShellDescriptor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service("DelegateSendProposal")
public class DelegateSendProposal implements JavaDelegate {

	private final String AAS_REGISTRYPATH = System.getenv().get("AAS_REGISTRY_URL");
	private final String SM_REGISTRYPATH = System.getenv().get("SM_REGISTRY_URL");
		
	@Override
    public void execute(DelegateExecution execution) {

		List<String> aasEndpointList = new ArrayList<>();
		DefaultAssetAdministrationShellDescriptor aasDescriptor = new DefaultAssetAdministrationShellDescriptor();
		List<ArrayList<String>> receiverEndpoints = new ArrayList<>();
		I4_0_message sendProposal_I40_messageObject = new I4_0_message();

		try {
			sendProposal_I40_messageObject.deserializeMsg(execution.getVariable("outgoingMessage", String.class));
                
            } catch (DeserializationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
			
		//change frame elements like sender and receiver for the outgoing message
        sendProposal_I40_messageObject = MsgParticipantServices.setFrameElements(sendProposal_I40_messageObject, 
													MessageType.offer.toString(), 
													sendProposal_I40_messageObject.receiver.getValue(), 
													sendProposal_I40_messageObject.sender.getValue(), 
													execution.getProcessInstanceId(), 
													sendProposal_I40_messageObject.messageId.getValue(), 
													sendProposal_I40_messageObject.conversationId.getValue(),
													"", 
													SemanticProtocol.VDI_2193_2.toString(), 
													"ServiceProvider");	

        execution.setVariable("form_interactionElements_SubmodelReferences", UIServices.getSMReferences(sendProposal_I40_messageObject.submodelReferences));
		execution.setVariable("form_interactionElements_DataElements", UIServices.getSMDataElements(sendProposal_I40_messageObject.dataElements));
		execution.setVariable("form_type", sendProposal_I40_messageObject.type.getValue());
		execution.setVariable("form_sender", sendProposal_I40_messageObject.sender.getValue());
		execution.setVariable("form_receiver", sendProposal_I40_messageObject.receiver.getValue());
		execution.setVariable("form_conversationId", sendProposal_I40_messageObject.conversationId.getValue());
		execution.setVariable("form_messageId", sendProposal_I40_messageObject.messageId.getValue());
		execution.setVariable("form_replyTo", sendProposal_I40_messageObject.replyTo.getValue());
		execution.setVariable("form_replyBy", sendProposal_I40_messageObject.replyBy.getValue());
		execution.setVariable("form_semanticProtocol", sendProposal_I40_messageObject.semanticProtocol.getValue());
		execution.setVariable("form_role", sendProposal_I40_messageObject.role.getValue());


        //save the proposal as interaction element in the I4.0 response message

		//get Message Participant Submodel Endpoint	
		aasDescriptor = RegistryAPI.getAASDescriptorByAASID(AAS_REGISTRYPATH, sendProposal_I40_messageObject.receiver.getValue());
		aasEndpointList = DescriptorServices.getAASEndpointsFromDescriptor(aasDescriptor);	
		receiverEndpoints = MsgParticipantServices.getProtocolSpecificReceiverEndpoints(aasEndpointList, SM_REGISTRYPATH, "ServiceRequester", "VDI_2193-2");

        //invoke the newMessage operation at the service requester AAS
		ArrayList<OperationVariable> inputArguments = new ArrayList<>();
		sendProposal_I40_messageObject.ov_frame.setValue(sendProposal_I40_messageObject.frameCollection);
		sendProposal_I40_messageObject.ov_interactionElements.setValue(sendProposal_I40_messageObject.interactionElementsCollection);
		inputArguments.add(sendProposal_I40_messageObject.ov_interactionElements);
		inputArguments.add(sendProposal_I40_messageObject.ov_frame);
		//As an easy use case it is assumed that there is only one endpoint for the submodel "MessageParticipant" which operation can be invoked
		//in complex cases this needs to be expanded 
		if(receiverEndpoints.size() <= 1 && receiverEndpoints.size() > 0){
			OperationServices.invokeOperation(receiverEndpoints.get(0).get(3).toString(), "newMessage", inputArguments);
		}
    }
}
