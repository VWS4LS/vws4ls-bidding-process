package org.flowable.ui.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import org.aas.http.api.RegistryAPI;
import org.aas.message.*;
import org.aas.services.DescriptorServices;
import org.aas.services.MsgParticipantServices;
import org.aas.services.OperationServices;
import org.aas.services.SimpleServices;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;


@Service("DelegateReadMessage")
public class DelegateReadMessage implements JavaDelegate {

	private final String AAS_REGISTRYPATH = System.getenv().get("AAS_REGISTRY_URL");
	private final String SM_REGISTRYPATH = System.getenv().get("SM_REGISTRY_URL");
	private String semanticID_SMMessageParticipant = "http://vws4ls.com/sample/submodel/type/messageParticipant/1/0/Submodel";
	public static DefaultOperation I40_messageOperation = new DefaultOperation();
	private I4_0_message readMessage_I40_messageObject;// = new I4_0_message();
	private DefaultOperation determineFeasibleScopeOperation = new DefaultOperation();

	@Override
    public void execute(DelegateExecution execution) {

		List<String> aasEndpointList = new ArrayList<>();
		DefaultAssetAdministrationShellDescriptor aasDescriptor = new DefaultAssetAdministrationShellDescriptor();
		String requestedAASID = execution.getVariableInstance("receiver").getTextValue();

		//get AAS-Endpoint	
		aasDescriptor = RegistryAPI.getAASDescriptorByAASID(AAS_REGISTRYPATH, requestedAASID);		
		aasEndpointList = DescriptorServices.getAASEndpointsFromDescriptor(aasDescriptor);

		//Read the operations "determineFeasibleScope" and "newMessage" to get basic configuration data of operation variables
		determineFeasibleScopeOperation = OperationServices.getOperationDataStructure(aasEndpointList, SM_REGISTRYPATH, semanticID_SMMessageParticipant, "determineFeasibleScope");
		I40_messageOperation = OperationServices.getOperationDataStructure(aasEndpointList, SM_REGISTRYPATH, semanticID_SMMessageParticipant, "newMessage");

		//fill default message object for current delegate
		readMessage_I40_messageObject = MsgParticipantServices.getDefault_I40_MessageObject(I40_messageOperation);

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

		//set gateway in flowable
		if (readMessage_I40_messageObject.conversationId == null || readMessage_I40_messageObject.conversationId.toString().compareTo("")==0) {
			execution.setVariable("message", "notUnderstood");
			execution.setVariable("resultBiddingProcess", "The request was not understood.");
		}else{
			execution.setVariable("message", "understood");
		}

		//Prepare output for flowable UI
		List<SubmodelElement> submodelReferencesList = new ArrayList<>();
		List<SubmodelElement> dataElementsList = new ArrayList<>();
		Set<String> variableNames = execution.getVariableNames();
		String smcReferences = "";
		String smcDataElements = "";
		
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
		readMessage_I40_messageObject.submodelReferences.setValue(submodelReferencesList);
		readMessage_I40_messageObject.dataElements.setValue(dataElementsList);

		execution.setVariable("form_interactionElements_SubmodelReferences", smcReferences);
		execution.setVariable("form_interactionElements_DataElements", smcDataElements);
		execution.setVariable("form_type", readMessage_I40_messageObject.type.getValue());
		execution.setVariable("form_sender", readMessage_I40_messageObject.sender.getValue());
		execution.setVariable("form_receiver", readMessage_I40_messageObject.receiver.getValue());
		execution.setVariable("form_conversationId", readMessage_I40_messageObject.conversationId.getValue());
		execution.setVariable("form_messageId", readMessage_I40_messageObject.messageId.getValue());
		execution.setVariable("form_replyTo", readMessage_I40_messageObject.replyTo.getValue());
		execution.setVariable("form_replyBy", readMessage_I40_messageObject.replyBy.getValue());
		execution.setVariable("form_semanticProtocol", readMessage_I40_messageObject.semanticProtocol.getValue());
		execution.setVariable("form_role", readMessage_I40_messageObject.role.getValue());

		//set incomming message variable in flowable
		execution.setVariable("requestedAASID", requestedAASID);
		
		try {
			execution.setVariable("incommingMessage", readMessage_I40_messageObject.serialize());
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		execution.setVariable("feasibleScope", SimpleServices.serialize(determineFeasibleScopeOperation));
    }
}
