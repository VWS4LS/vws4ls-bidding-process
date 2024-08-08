package org.aas.services;

import java.util.ArrayList;
import java.util.List;

import org.aas.http.api.RegistryAPI;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.aas4j.v3.model.Endpoint;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShellDescriptor;

public class DescriptorServices {

	public static List<String> getAASEndpointList(String aasRegistryPathAsString){
		List<String> aasEndpointList = new ArrayList<>();
		//Connect to AAS Regsitry and read all registered AASs
		List<DefaultAssetAdministrationShellDescriptor> aasDescriptorList = RegistryAPI.getAllShellDescriptors(aasRegistryPathAsString);

		//for every AAS descriptor read AAS Endpoint
		for (DefaultAssetAdministrationShellDescriptor aasDescriptor : aasDescriptorList) {
			List<String> endpointList = getAASEndpointsFromDescriptor(aasDescriptor);
			aasEndpointList.addAll(endpointList);
		}
		return aasEndpointList;

	};

    public static List<String> getAASEndpointsFromDescriptor(AssetAdministrationShellDescriptor aasDescriptor){
		List<String> endpointList = new ArrayList<>();
		if (aasDescriptor.getEndpoints() != null){
			//get all AAS Endpoints
			for (Endpoint aasEndpoint : aasDescriptor.getEndpoints()) {
				//get AAS via AAS-Endpoint
				String endpointAsString = aasEndpoint.getProtocolInformation().getHref();
				endpointList.add(endpointAsString);
			}
		}
		else{
			System.out.println("Keine Endpunkte vorhanden");
		}
		return endpointList;
	};

	//For prototype only the last occuring endpoint is used
	public static String getSMEndpointFromDescriptor(SubmodelDescriptor smDescriptor){
		String endpoint = "";
		if (smDescriptor.getEndpoints() != null){
			//get all AAS Endpoints
			for (Endpoint smEndpoint : smDescriptor.getEndpoints()) {
				//get AAS via AAS-Endpoint
				endpoint = smEndpoint.getProtocolInformation().getHref();
			}
		}
		else{
			endpoint = "";
			System.out.println("Keine Endpunkte vorhanden");
		}
		return endpoint;
	};

	
}
