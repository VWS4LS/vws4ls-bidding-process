package org.flowable.ui.application;

import java.util.ArrayList;
import java.util.List;

import org.aas.http.api.RegistryAPI;
import org.aas.message.I4_0_message;
import org.aas.services.DescriptorServices;
import org.aas.services.MsgParticipantServices;
import org.aas.services.OperationServices;
import org.aas.services.SimpleServices;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShellDescriptor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import liquibase.pro.packaged.an;


@Service("DelegateSendDecision")
public class DelegateSendDecision implements JavaDelegate {

	private final String AAS_REGISTRYPATH = System.getenv().get("AAS_REGISTRY_URL");
	private final String SM_REGISTRYPATH = System.getenv().get("SM_REGISTRY_URL");
	private String[] choosedProposalString;
	private I4_0_message[] choosedProposal_I4_0_messagObjects;

	@Override
    public void execute(DelegateExecution execution) {
		
		if (execution.getVariable("expectedMessageCounter",Integer.class) != 0){

			//get the string with all collected proposals
			try {
				choosedProposalString = SimpleServices.deserializeCollectedObjects(execution.getVariable("proposalListWithDecision", String.class));
				choosedProposal_I4_0_messagObjects = new I4_0_message[choosedProposalString.length];
			} catch (Exception e) {
				System.out.println("No proposals collected.");
				execution.setVariable("form_status", "Only refusals received.");
			}

			//convert message string into message objects
            choosedProposal_I4_0_messagObjects = SimpleServices.splitMsgStringListIntoMsgObjectList(choosedProposalString);

			
			for(I4_0_message answer : choosedProposal_I4_0_messagObjects){ 

				List<String> aasEndpointList = new ArrayList<>();
				DefaultAssetAdministrationShellDescriptor aasDescriptor = new DefaultAssetAdministrationShellDescriptor();
				List<ArrayList<String>> receiverEndpoints = new ArrayList<>();

				//get Message Participant Submodel Endpoint	
				aasDescriptor = RegistryAPI.getAASDescriptorByAASID(AAS_REGISTRYPATH, answer.receiver.getValue());
				aasEndpointList = DescriptorServices.getAASEndpointsFromDescriptor(aasDescriptor);	
				receiverEndpoints = MsgParticipantServices.getProtocolSpecificReceiverEndpoints(aasEndpointList, SM_REGISTRYPATH, "ServiceProvider", "VDI_2193-2");

				//invoke the newMessage operation at the service requester AAS
				ArrayList<OperationVariable> inputArguments = new ArrayList<>();
				answer.ov_frame.setValue(answer.frameCollection);
				answer.ov_interactionElements.setValue(answer.interactionElementsCollection);
				inputArguments.add(answer.ov_interactionElements);
				inputArguments.add(answer.ov_frame);

				//As an easy use case it is assumed that there is only one endpoint for the submodel "MessageParticipant" which operation can be invoked
				//in complex cases this needs to be expanded 
				if(receiverEndpoints.size() <= 1 && receiverEndpoints.size() > 0){
					OperationServices.invokeOperation(receiverEndpoints.get(0).get(3).toString(), "newMessage", inputArguments);
				}
			}
		}
    }
}