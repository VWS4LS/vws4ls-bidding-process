package org.aas.services;


import java.util.ArrayList;
import java.util.List;

import org.aas.http.api.RegistryAPI;
import org.aas.http.api.RepositoryAPI;
import org.aas.message.I4_0_message;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelDescriptor;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;

public class MsgParticipantServices {

	private static String semanticID_SMMessageParticipant = "http://vws4ls.com/sample/submodel/type/messageParticipant/1/0/Submodel";
	private static String semanticID_SMMessageParticipant_InteractionElements = "http://vws4ls.com/sample/submodel/type/messagePaticipant/interactionElements/1/0";
	private static String semanticID_SMMessageParticipant_SubmodelReferences = "http://vws4ls.com/sample/submodel/type/messagePaticipant/submodelReferences/1/0";
	private static String semanticID_SMMessageParticipant_DataElements = "http://vws4ls.com/sample/submodel/type/messagePaticipant/dataElements/1/0";
	private static String semanticID_SMMessageParticipant_Frame = "http://vws4ls.com/sample/submodel/type/messagePaticipant/frame/1/0";

	public static List<ArrayList<String>> getProtocolSpecificReceiverEndpoints(List<String> aasEndpointsAsStringList, String smRegistryPathAsString, String roleAsString, String semanticProtcolAsString){
		List<ArrayList<String>> receiverEndpointList = new ArrayList<>();

		//for every AAS Endpoint read  submodel endpoints of AAS with semanticId "Message Participant"
		for (String aasEndpoint : aasEndpointsAsStringList) {
			
			//get AAS via AAS-Endpoint
			DefaultAssetAdministrationShell aas = RepositoryAPI.getAASbyEndpoint(aasEndpoint);
			
			//read all Submodel References of the AAS
			if (aas.getSubmodels() != null){
				
				for (Reference smReference : aas.getSubmodels()) {
					
					//get Submodel Descriptors for Submodel References
					for (Key key : smReference.getKeys()) {
						
						DefaultSubmodelDescriptor smDescriptor = RegistryAPI.getSMDescriptorBySMID(smRegistryPathAsString, key.getValue());

						//Read the subdmodelElementList for semantic protocols of the submodel with semanticId (http://vws4ls.com/sample/submodel/type/messagePaticipant/semanticProtocols/1/0) 
						if(smDescriptor.getSemanticId() != null){
							if(smDescriptor.getSemanticId().getKeys().get(0).getValue().compareTo(semanticID_SMMessageParticipant) == 0){

								//Choose the AAS with submodel thats semanticProtocol is "ServiceProvider" and the semantic protocol "VDI_2193-2"
								if (findSemanticProtocol(DescriptorServices.getSMEndpointFromDescriptor(smDescriptor), "semanticProtocols", roleAsString, semanticProtcolAsString)){
									
									//Save the AAS-ID and SM-ID for following operation invocation 
									ArrayList<String> endpoints = new ArrayList<String>();
									String smEndpoint = DescriptorServices.getSMEndpointFromDescriptor(smDescriptor);
									//add AAS Endpoint for AAS with MessageParticipant
									endpoints.add(0, aas.getId());
									endpoints.add(1, aasEndpoint);

									//add SM Endpoint with Message Participant
									endpoints.add(2, smDescriptor.getId());
									endpoints.add(3, smEndpoint);

									//add AAS and SM Endpoint to Receiver List
									receiverEndpointList.add(endpoints);
								} 
							}
						}
					}
				}
			}
		}

		return receiverEndpointList;
	};

    public static boolean findSemanticProtocol(String submodelEndpointAsString, String idShortOfSubmodelElementListAsString, String roleAsString, String protocolAsString){
		boolean result = false;
		SubmodelElementList smElementList = new DefaultSubmodelElementList();

		//get whole Submodel Element List
		smElementList = RepositoryAPI.getSubmodelElementList(submodelEndpointAsString, idShortOfSubmodelElementListAsString);

		for (int i = 1; i <= smElementList.getValue().size(); i++){

			//compare List Element against condition
			if(smElementList.getValue().toString().contains(roleAsString) && smElementList.getValue().toString().contains(protocolAsString)){
				result = true;
			}
			else{
				result = false;
			}
		}
		return result;
	};

	public static I4_0_message getDefault_I40_MessageObject(DefaultOperation operation){
		I4_0_message returnMessageObject = new I4_0_message();

		//fill default message object
		for (OperationVariable var : operation.getInputVariables()) {
			if (var.getValue().getSemanticId().getKeys().get(0).getValue().compareTo(semanticID_SMMessageParticipant_InteractionElements) == 0){
				returnMessageObject.ov_interactionElements = (DefaultOperationVariable) var;
				returnMessageObject.interactionElementsCollection = (DefaultSubmodelElementCollection) returnMessageObject.ov_interactionElements.getValue();
				for (SubmodelElement collection : returnMessageObject.interactionElementsCollection.getValue()){
					if (collection.getSemanticId().getKeys().get(0).getValue().compareTo(semanticID_SMMessageParticipant_SubmodelReferences) == 0){
						returnMessageObject.submodelReferences = (DefaultSubmodelElementCollection) collection;
					} else if (collection.getSemanticId().getKeys().get(0).getValue().compareTo(semanticID_SMMessageParticipant_DataElements) == 0){
						returnMessageObject.dataElements = (DefaultSubmodelElementCollection) collection;
					} 
				}
			}
			else if (var.getValue().getSemanticId().getKeys().get(0).getValue().compareTo(semanticID_SMMessageParticipant_Frame) == 0){
				returnMessageObject.ov_frame = (DefaultOperationVariable) var;
			}
			else {
				returnMessageObject = null;
			}
		}
		return returnMessageObject;
	};

	public static I4_0_message setOperationDefinedInteractionElements(Operation userOperationWithInteractionElements, I4_0_message I40_messageObject){
		
		//Node-Red doesn't transfer complex data objects into flowable so that flowable has to read the inputVariables for "startBiddingProcess" seperatly
		for (OperationVariable var : userOperationWithInteractionElements.getInputVariables()){
			if (var.getValue().getSemanticId().getKeys().get(0).getValue().compareTo(semanticID_SMMessageParticipant_InteractionElements) == 0){
				
				DefaultSubmodelElementCollection SMC_userOperation = new DefaultSubmodelElementCollection();
				ArrayList<SubmodelElement> SMC_List = new ArrayList<>();
				SMC_userOperation = (DefaultSubmodelElementCollection) var.getValue();

				for (SubmodelElement SME : SMC_userOperation.getValue()){
					if (SME.getSemanticId().getKeys().get(0).getValue().compareTo(semanticID_SMMessageParticipant_SubmodelReferences) == 0){
						I40_messageObject.submodelReferences = (DefaultSubmodelElementCollection) SME;
						
					}
					else if (SME.getSemanticId().getKeys().get(0).getValue().compareTo(semanticID_SMMessageParticipant_DataElements) == 0){
						I40_messageObject.dataElements = (DefaultSubmodelElementCollection) SME;
						
					}
					else{
						System.out.println("There exists no input variables of type \"submodelReferences\" and \"dataElements\"");
					}
				}
				SMC_List.add(I40_messageObject.submodelReferences);
				SMC_List.add(I40_messageObject.dataElements);
				I40_messageObject.interactionElementsCollection.setValue(SMC_List);
				I40_messageObject.ov_interactionElements.setValue(I40_messageObject.interactionElementsCollection);	
			}			
		}

		return I40_messageObject;
	} ;

	public static I4_0_message setFrameElements(I4_0_message I40_messageObject, String receiverValueAsString){
		for (SubmodelElement SME : I40_messageObject.frameCollection.getValue()){
			DefaultProperty prop = new DefaultProperty();
			prop = (DefaultProperty) SME;
			String idShort = prop.getIdShort();
			switch (idShort) {
				case "receiver":
					prop.setValue(receiverValueAsString);
					I40_messageObject.receiver = prop;
					break;
				default:
					break;
			}
		}
		return I40_messageObject;
	};

	public static I4_0_message setFrameElements(I4_0_message I40_messageObject){
			for (SubmodelElement SME : I40_messageObject.frameCollection.getValue()){
				DefaultProperty prop = new DefaultProperty();
				prop = (DefaultProperty) SME;
				String idShort = prop.getIdShort();
				switch (idShort) {
					case "type":
						I40_messageObject.type = prop;
						break;
					case "sender":
						I40_messageObject.sender = prop;
						break;
					case "receiver":
						I40_messageObject.receiver = prop;
						break;
					case "conversationId":
						I40_messageObject.conversationId = prop;
						break;
					case "messageId":
						I40_messageObject.messageId = prop;
						break;
					case "replyTo":
						I40_messageObject.replyTo = prop;
						break;
					case "replyBy":
						I40_messageObject.replyBy = prop;
						break;
					case "semanticProtocol":
						I40_messageObject.semanticProtocol = prop;
						break;
					case "role":
						I40_messageObject.role = prop;
						break;
					default:
						break;
				}
			}
		return I40_messageObject;
	};

	public static I4_0_message setFrameElements(I4_0_message I40_messageObject, 
												String typeValueAsString, 
												String senderValueAsString,
												String receiverValuesAsString,
												String conversationIdValueAsString,
												String messageIdValueAsString,
												String replyToValueAsString,
												String replyByValueAsString,
												String semanticProtocolValueAsString,
												String roleValueAsString ){
			for (SubmodelElement SME : I40_messageObject.frameCollection.getValue()){
				DefaultProperty prop = new DefaultProperty();
				prop = (DefaultProperty) SME;
				String idShort = prop.getIdShort();
				switch (idShort) {
					case "type":
						prop.setValue(typeValueAsString);
						I40_messageObject.type = prop;
						break;
					case "sender":
						prop.setValue(senderValueAsString);
						I40_messageObject.sender = prop;
						break;
					case "receiver":
						prop.setValue(receiverValuesAsString);
						I40_messageObject.receiver = prop;
						break;
					case "conversationId":
						prop.setValue(conversationIdValueAsString);
						I40_messageObject.conversationId = prop;
						break;
					case "messageId":
						prop.setValue(messageIdValueAsString);
						I40_messageObject.messageId = prop;
						break;
					case "replyTo":
						prop.setValue(replyToValueAsString);
						I40_messageObject.replyTo = prop;
						break;
					case "replyBy":
						prop.setValue(replyByValueAsString);
						I40_messageObject.replyBy = prop;
						break;
					case "semanticProtocol":
						prop.setValue(semanticProtocolValueAsString);
						I40_messageObject.semanticProtocol = prop;
						break;
					case "role":
						prop.setValue(roleValueAsString);
						I40_messageObject.role = prop;
						break;
					default:
						break;
				}
			}
		return I40_messageObject;
	};


	
}
