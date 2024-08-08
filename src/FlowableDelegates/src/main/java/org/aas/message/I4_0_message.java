package org.aas.message;


import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;


import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;

public class I4_0_message{
    
    public DefaultSubmodelElementCollection submodelReferences;    //SubmodelElementCollection including necessary submodel references
    public DefaultSubmodelElementCollection dataElements;          //SubmodelElementCollection including necessary data elements
    public DefaultProperty type;                         //Intention of the message, according to type list of VDI 2193.                     
    public DefaultProperty sender;                       //Sender for message (AAS-ID).
    public DefaultProperty receiver;                     //Receiver for message (AAS-ID).
    public DefaultProperty conversationId;               //ID of a conversation.
    public DefaultProperty messageId;                    //ID of a message.
    public DefaultProperty replyTo;                      //Expression referencing the original message.
    public DefaultProperty replyBy;                      //Time by which the response must be available.
    public DefaultProperty semanticProtocol;             //Identification of the semantic protocol, for example according to VDI/VDE 2193-2.
    public DefaultProperty role;                         //Task of the sender of the message in the semantic protocol.
    public DefaultOperationVariable ov_interactionElements;
    public DefaultSubmodelElementCollection interactionElementsCollection; //variable to push all interactionElement variables into one operation variable
    public DefaultOperationVariable ov_frame;
    public DefaultSubmodelElementCollection frameCollection; //variable to push all interactionElement variables into one operation variable

    public int counter;

    public I4_0_message(){

        //instantiate the lowest level variables
        this.submodelReferences = new DefaultSubmodelElementCollection();
        this.dataElements = new DefaultSubmodelElementCollection();
        this.type = new DefaultProperty();
        this.sender = new DefaultProperty();
        this.receiver = new DefaultProperty();
        this.conversationId = new DefaultProperty();
        this.messageId = new DefaultProperty();
        this.replyTo = new DefaultProperty();
        this.replyBy = new DefaultProperty();
        this.semanticProtocol = new DefaultProperty();
        this.role = new DefaultProperty();

        //instantiate the operation variable element and all all relevant low level variables
        this.interactionElementsCollection = new DefaultSubmodelElementCollection();

        this.frameCollection = new DefaultSubmodelElementCollection();

        //instantiate the operation variable and add operation variable element
        this.ov_interactionElements = new DefaultOperationVariable();
        this.ov_frame = new DefaultOperationVariable();
    }

    public String serialize() throws SerializationException{
        JsonSerializer serializer = new JsonSerializer();
        String resultMsgString = " json.msg.submodelReferences " + serializer.write(this.submodelReferences);
        resultMsgString += " json.msg.dataElements " + serializer.write(this.dataElements);
        resultMsgString += " json.msg.type " + serializer.write(this.type);
        resultMsgString += " json.msg.sender " + serializer.write(this.sender);
        resultMsgString += " json.msg.receiver " + serializer.write(this.receiver);
        resultMsgString += " json.msg.conversationId " + serializer.write(this.conversationId);
        resultMsgString += " json.msg.messageId " + serializer.write(this.messageId);
        resultMsgString += " json.msg.replyTo " + serializer.write(this.replyTo);
        resultMsgString += " json.msg.replyBy " + serializer.write(this.replyBy);
        resultMsgString += " json.msg.semanticProtocol " + serializer.write(this.semanticProtocol);
        resultMsgString += " json.msg.role " + serializer.write(this.role);
        resultMsgString += " json.msg.interactionElementsCollection " + serializer.write(this.interactionElementsCollection);
        resultMsgString += " json.msg.frameCollection " + serializer.write(this.frameCollection);
        resultMsgString += " json.msg.ov_interactionElements " + serializer.write(this.ov_interactionElements);
        resultMsgString += " json.msg.ov_frame " + serializer.write(this.ov_frame);      
        return resultMsgString;
    }

    public void deserializeMsg(String msgString) throws DeserializationException{
        JsonDeserializer deserializer = new JsonDeserializer();
        String[] singleMsgObjects = msgString.split(" json.");
            for (String obj : singleMsgObjects) {
                
                if(obj.contains("msg.submodelReferences")){
                    obj = obj.replace("msg.submodelReferences ", "");
                    this.submodelReferences = deserializer.read(obj, DefaultSubmodelElementCollection.class);
                }
                else if (obj.contains("msg.dataElements")){
                    obj = obj.replace("msg.dataElements ", "");
                    this.dataElements = deserializer.read(obj, DefaultSubmodelElementCollection.class);
                }
                else if (obj.contains("msg.type")){
                    obj = obj.replace("msg.type ", "");
                    this.type = deserializer.read(obj, DefaultProperty.class);
                }
                else if (obj.contains("msg.sender")){
                    obj = obj.replace("msg.sender ", "");
                    this.sender = deserializer.read(obj, DefaultProperty.class);
                }
                else if (obj.contains("msg.receiver")){
                    obj = obj.replace("msg.receiver ", "");
                    this.receiver = deserializer.read(obj, DefaultProperty.class);
                }
                else if (obj.contains("msg.conversationId")){
                    obj = obj.replace("msg.conversationId ", "");
                    this.conversationId = deserializer.read(obj, DefaultProperty.class);
                }
                else if (obj.contains("msg.messageId")){
                    obj = obj.replace("msg.messageId ", "");
                    this.messageId = deserializer.read(obj, DefaultProperty.class);
                }
                else if (obj.contains("msg.replyTo")){
                    obj = obj.replace("msg.replyTo ", "");
                    this.replyTo = deserializer.read(obj, DefaultProperty.class);
                }
                else if (obj.contains("msg.replyBy")){
                    obj = obj.replace("msg.replyBy ", "");
                    this.replyBy = deserializer.read(obj, DefaultProperty.class);
                }
                else if (obj.contains("msg.semanticProtocol")){
                    obj = obj.replace("msg.semanticProtocol ", "");
                    this.semanticProtocol = deserializer.read(obj, DefaultProperty.class);
                }
                else if (obj.contains("msg.role")){
                    obj = obj.replace("msg.role ", "");
                    this.role = deserializer.read(obj, DefaultProperty.class);
                }
                else if (obj.contains("msg.interactionElementsCollection")){
                    obj = obj.replace("msg.interactionElementsCollection ", "");
                    this.interactionElementsCollection = deserializer.read(obj, DefaultSubmodelElementCollection.class);
                }
                else if (obj.contains("msg.frameCollection")){
                    obj = obj.replace("msg.frameCollection ", "");
                    this.frameCollection = deserializer.read(obj, DefaultSubmodelElementCollection.class);
                }
                else if (obj.contains("msg.ov_interactionElements")){
                    obj = obj.replace("msg.ov_interactionElements ", "");
                    this.ov_interactionElements = deserializer.read(obj, DefaultOperationVariable.class);
                }
                else if (obj.contains("msg.ov_frame")){
                    obj = obj.replace("msg.ov_frame ", "");
                    this.ov_frame = deserializer.read(obj, DefaultOperationVariable.class);
                }
                else{
                    System.out.println("Deserialization of message of object[" + obj + "] failed.");
                }
            }
        
    }
    
}