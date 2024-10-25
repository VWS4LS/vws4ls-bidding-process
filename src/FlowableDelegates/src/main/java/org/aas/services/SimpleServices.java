package org.aas.services;

import java.util.ArrayList;
import java.util.List;

import org.aas.message.I4_0_message;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReferenceElement;

public class SimpleServices {
    public static DefaultReferenceElement findReferenceElementWithIdShort(List<SubmodelElement> referenceElementList, String IdShort){

        DefaultReferenceElement referenceElement = new DefaultReferenceElement();
        for(SubmodelElement element : referenceElementList){
            if(element.getIdShort().compareTo(IdShort) == 0){
                referenceElement = (DefaultReferenceElement) element; 
            }
        }

        return referenceElement;
    };

    public static DefaultReferenceElement setReferenceElement(String referenceIdShort, String referenceValue){

        DefaultReferenceElement element = new DefaultReferenceElement();
        Reference ref = new DefaultReference();
        Key refKey = new DefaultKey();
        List<Key> keyList = new ArrayList<>();
        
        element.setIdShort(referenceIdShort);
        refKey.setValue(referenceValue);
        refKey.setType(KeyTypes.SUBMODEL);
        keyList.add(refKey);
        ref.setKeys(keyList);
        ref.setType(ReferenceTypes.EXTERNAL_REFERENCE);
        element.setValue(ref);
            
        return element;
    };

    public static DefaultProperty setDataElement(String dataElementIdShort, String dataElementValue){

        DefaultProperty element = new DefaultProperty();
        element.setIdShort(dataElementIdShort);
        element.setValue(dataElementValue);
    
        return element;
    };

    public static String serialize(SubmodelElement sme){
	JsonSerializer serializer = new JsonSerializer();
	String jsonMessage = "";
	try {
		jsonMessage = serializer.write(sme);
	} catch (SerializationException e) {
		e.printStackTrace();
	}	
		return jsonMessage;
	}

    public static String serializeCollectedObjects(String object, String previousCollectedObjects){
        return previousCollectedObjects += object + "NEXT";
    }

    public static String[] deserializeCollectedObjects(String collectedObjects){
        String[] splittedObjects = collectedObjects.split("NEXT");
        return splittedObjects;
    }

    public static I4_0_message[] splitMsgStringListIntoMsgObjectList (String[] msgStringList){
        I4_0_message[] msgObjectList = new I4_0_message[msgStringList.length];
        for (int i = 0; i < msgStringList.length; i++) {
                String proposalString = msgStringList[i];
                msgObjectList[i] = new I4_0_message();
                try {
                    msgObjectList[i].deserializeMsg(proposalString);
                    
                } catch (DeserializationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } 
            return msgObjectList;
    }

    
}
