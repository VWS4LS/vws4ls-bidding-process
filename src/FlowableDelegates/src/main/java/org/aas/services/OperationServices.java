package org.aas.services;

import java.util.ArrayList;
import java.util.List;

import org.aas.http.api.RegistryAPI;
import org.aas.http.api.RepositoryAPI;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelDescriptor;

public class OperationServices {

    private static JsonSerializer serializer = new JsonSerializer();

	public static DefaultOperation getOperationDataStructure(List<String> aasEndpointsAsStringList, String smRegistryPathAsString, String semanticIDOfProtcolAsString, String operationAsString){
		DefaultOperation op = new DefaultOperation();

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

						//Read the submodel with given semanticId 
						if (smDescriptor.getSemanticId() != null){
							if(smDescriptor.getSemanticId().getKeys().get(0).getValue().compareTo(semanticIDOfProtcolAsString) == 0){

								//Read operation structure
								String smEndpoint = DescriptorServices.getSMEndpointFromDescriptor(smDescriptor);
								op = (DefaultOperation) RepositoryAPI.getOperation(smEndpoint, operationAsString);
							}
						}
						
					}
				}
			}
		}

		return op;
	};

    public static void invokeOperation (String submodelEndpointAsString, String idShortPathAsString, ArrayList<OperationVariable> inputOperationVariableList){
		
		Operation op = new DefaultOperation();
        String serializedOp = "";
		op.setInputVariables(inputOperationVariableList);
		try {
			serializedOp = serializer.write(op);
		} catch (SerializationException e) {
			e.printStackTrace();
		}
		//cleaning serialized object for http request 
		serializedOp = serializedOp.replace("inputVariables", "inputArguments");

		RepositoryAPI.invokeOperation(submodelEndpointAsString, idShortPathAsString, serializedOp);
	}
}
