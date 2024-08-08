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
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShellDescriptor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;


@Service("DelegateSendConfirmation")
public class DelegateSendConfirmation implements JavaDelegate {

	private final String AAS_REGISTRYPATH = System.getenv().get("AAS_REGISTRY_URL");
    private final String SM_REGISTRYPATH = System.getenv().get("SM_REGISTRY_URL");

	@Override
    public void execute(DelegateExecution execution) {
		
        List<String> aasEndpointList = new ArrayList<>();
		DefaultAssetAdministrationShellDescriptor aasDescriptor = new DefaultAssetAdministrationShellDescriptor();
		List<ArrayList<String>> receiverEndpoints = new ArrayList<>();

        I4_0_message sendConfirmation_I40_messageObject = new I4_0_message();

		try {
			sendConfirmation_I40_messageObject.deserializeMsg(execution.getVariable("outgoingMessage", String.class));
                
            } catch (DeserializationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
		
        // preprare incomming message for outgoing message event
        sendConfirmation_I40_messageObject = MsgParticipantServices.setFrameElements(sendConfirmation_I40_messageObject, 
													MessageType.confirming.toString(), 
													sendConfirmation_I40_messageObject.receiver.getValue(), 
													sendConfirmation_I40_messageObject.sender.getValue(), 
													execution.getProcessInstanceId(), 
													sendConfirmation_I40_messageObject.messageId.getValue(), 
													sendConfirmation_I40_messageObject.conversationId.getValue(),
													"", 
													SemanticProtocol.VDI_2193_2.toString(), 
													"ServiceProvider");

        //get Message Participant Submodel Endpoint	
		aasDescriptor = RegistryAPI.getAASDescriptorByAASID(AAS_REGISTRYPATH, sendConfirmation_I40_messageObject.receiver.getValue());
		aasEndpointList = DescriptorServices.getAASEndpointsFromDescriptor(aasDescriptor);	
		receiverEndpoints = MsgParticipantServices.getProtocolSpecificReceiverEndpoints(aasEndpointList, SM_REGISTRYPATH, "ServiceRequester", "VDI_2193-2");

        //invoke the newMessage operation at the service requester AAS
		ArrayList<OperationVariable> inputArguments = new ArrayList<>();
		sendConfirmation_I40_messageObject.ov_frame.setValue(sendConfirmation_I40_messageObject.frameCollection);
		sendConfirmation_I40_messageObject.ov_interactionElements.setValue(sendConfirmation_I40_messageObject.interactionElementsCollection);
		inputArguments.add(sendConfirmation_I40_messageObject.ov_interactionElements);
		inputArguments.add(sendConfirmation_I40_messageObject.ov_frame);
		//As an easy use case it is assumed that there is only one endpoint for the submodel "MessageParticipant" which operation can be invoked
		//in complex cases this needs to be expanded 
		if(receiverEndpoints.size() <= 1 && receiverEndpoints.size() > 0){
			OperationServices.invokeOperation(receiverEndpoints.get(0).get(3).toString(), "newMessage", inputArguments);
		}

    }
}