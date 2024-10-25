package org.flowable.ui.application;

import java.util.*;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelDescriptor;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.aas.enumeration.MessageType;
import org.aas.enumeration.SemanticProtocol;
import org.aas.http.api.RegistryAPI;
import org.aas.http.api.RepositoryAPI;
import org.aas.message.*;
import org.aas.services.*;

@Service("DelegateCreateCFP")
public class DelegateCreateCFP implements JavaDelegate {

	private final String AAS_REGISTRYPATH = System.getenv().get("AAS_REGISTRY_URL");
	private final String SM_REGISTRYPATH = System.getenv().get("SM_REGISTRY_URL");
	private String semanticID_SMMessageParticipant = "http://vws4ls.com/sample/submodel/type/messageParticipant/1/0/Submodel";
	private DefaultOperation userOperation = new DefaultOperation();
	private DefaultOperation I40_messageOperation = new DefaultOperation();
	private I4_0_message I40_messageObject = new I4_0_message();
	
	@Override
    public void execute(DelegateExecution execution) {
		
		List<String> aasEndpointList = new ArrayList<>();
		DefaultAssetAdministrationShellDescriptor aasDescriptor = new DefaultAssetAdministrationShellDescriptor();
		String requestingAASID = execution.getVariableInstance("reqAasId").getTextValue();

		//get AAS-Endpoint	
		aasDescriptor = RegistryAPI.getAASDescriptorByAASID(AAS_REGISTRYPATH, requestingAASID);		
		aasEndpointList = DescriptorServices.getAASEndpointsFromDescriptor(aasDescriptor);

		//Read the operation "startBiddingProcess" and "newMessage" to get basic configuration data of operation variables
		userOperation = OperationServices.getOperationDataStructure(aasEndpointList, SM_REGISTRYPATH, semanticID_SMMessageParticipant, "startBiddingProcess");
		I40_messageOperation = OperationServices.getOperationDataStructure(aasEndpointList, SM_REGISTRYPATH, semanticID_SMMessageParticipant, "newMessage");

		//fill default message object for current delegate
		I40_messageObject = MsgParticipantServices.getDefault_I40_MessageObject(I40_messageOperation);

		//store empty message object as template for further delegates 
		try {
			execution.setVariable("msgTemplate", I40_messageObject.serialize());
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Get interactionElements from operation "startBiddingProcess"
		I40_messageObject = MsgParticipantServices.setOperationDefinedInteractionElements(userOperation, I40_messageObject);

		//get the ProductTypeReference of the submodel ProductionOrder and overwrite the submodel reference to the submodel ProductionOrder
		DefaultSubmodelElementCollection smcSubmodelReferences = I40_messageObject.submodelReferences;
		List<SubmodelElement> smcCopy = smcSubmodelReferences.getValue();
		for(int i = 0; i < smcCopy.size(); i++){
			DefaultReferenceElement ref = (DefaultReferenceElement) smcCopy.get(i);
			if (ref.getIdShort().compareTo("ProductionOrder") == 0){
				DefaultSubmodelDescriptor smDescriptor = RegistryAPI.getSMDescriptorBySMID(SM_REGISTRYPATH, ref.getValue().getKeys().get(0).getValue());
				String smEndpoint = DescriptorServices.getSMEndpointFromDescriptor(smDescriptor);
				DefaultReferenceElement productTypeReference = RepositoryAPI.getSubmodelReferenceElement(smEndpoint, "ProductTypeReference");
				ref = productTypeReference;
				smcCopy.set(i, ref);
				//delete local varibale in flowable because it wont be used in the later process steps
				execution.removeVariable("submodelReference_ProductionOrder");
			}
		}
		I40_messageObject.submodelReferences.setValue(smcCopy);
		
		
		//complete message object with frame values
		I40_messageObject.frameCollection = (DefaultSubmodelElementCollection) I40_messageObject.ov_frame.getValue();
		I40_messageObject = MsgParticipantServices.setFrameElements(I40_messageObject, 
													MessageType.call_for_proposals.toString(), 
													aasDescriptor.getId(), 
													"", 
													execution.getProcessInstanceId(), 
													"proposalReceived", 
													"",
													"", 
													SemanticProtocol.VDI_2193_2.toString(), 
													"ServiceRequester");

		System.out.println(("Execution ID: " + execution.getProcessInstanceId()));

		execution.setVariable("form_interactionElements_SubmodelReferences", UIServices.getSMReferences(I40_messageObject.submodelReferences));
		execution.setVariable("form_interactionElements_DataElements", UIServices.getSMDataElements(I40_messageObject.dataElements));
		execution.setVariable("form_type", I40_messageObject.type.getValue());
		execution.setVariable("form_sender", I40_messageObject.sender.getValue());
		execution.setVariable("form_receiver", I40_messageObject.receiver.getValue());
		execution.setVariable("form_conversationId", I40_messageObject.conversationId.getValue());
		execution.setVariable("form_messageId", I40_messageObject.messageId.getValue());
		execution.setVariable("form_replyTo", I40_messageObject.replyTo.getValue());
		execution.setVariable("form_replyBy", I40_messageObject.replyBy.getValue());
		execution.setVariable("form_semanticProtocol", I40_messageObject.semanticProtocol.getValue());
		execution.setVariable("form_role", I40_messageObject.role.getValue());

		try {
			execution.setVariable("outgoingMessage", I40_messageObject.serialize());
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
    } 
}
