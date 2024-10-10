package org.flowable.ui.application;

import org.aas.enumeration.MessageType;
import org.aas.http.api.NodeRedAPI;
import org.aas.message.I4_0_message;
import org.aas.services.MsgParticipantServices;
import org.aas.services.SimpleServices;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service("DelegateChooseProposal")
public class DelegateChooseProposal implements JavaDelegate {

    public String[] collectedProposalsString;
	public static I4_0_message[] chooseProposal_I40_messageObjects;
    public static int expectedMessageCounter = 0;

	@Override
    public void execute(DelegateExecution execution) {
		
        execution.setVariable("form_selectionStrategy", DelegateCreateCFP.selectionStrategy);
        //invoke the best selection algorithm on the proposals. What is needed for the interface?
        //the result will be a list with receiverID and decision

        
        //get the string with all collected proposals
        collectedProposalsString = SimpleServices.deserializeCollectedObjects(execution.getVariable("allCollectedProposals", String.class));
        chooseProposal_I40_messageObjects = new I4_0_message[collectedProposalsString.length];

        //split the proposals string into single proposals an deserialize it as I4.0 message and add it to the array list "chooseProposal.."
        for (int i = 0; i < collectedProposalsString.length; i++) {
            String proposalString = collectedProposalsString[i];
            chooseProposal_I40_messageObjects[i] = new I4_0_message();
            try {
                chooseProposal_I40_messageObjects[i].deserializeMsg(proposalString);
                
            } catch (DeserializationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } 

        //Invoke AAS operation "selectBestProposals" with input variables
		String output = NodeRedAPI.invokeSelectBestProposals(chooseProposal_I40_messageObjects, DelegateCreateCFP.selectionStrategy);

        //if the decision ist "true" then an offer acceptance will be send
        //else an offer rejection will be send
        for(I4_0_message proposal : chooseProposal_I40_messageObjects) {

            String msgType = "";
            if(output.contains(proposal.sender.getValue())){
                msgType = MessageType.offer_acceptance.toString();

                //counting the accepted proposals for later checking of confirmation status
                //counting the incomming confirmations
		        expectedMessageCounter = execution.getVariable("expectedMessageCounter",Integer.class);
                expectedMessageCounter++; 
                execution.setVariable("expectedMessageCounter", expectedMessageCounter);
            } else {
                msgType = MessageType.offer_rejection.toString();
            }
            
            proposal = MsgParticipantServices.setFrameElements(proposal);
            proposal.interactionElementsCollection = proposal.interactionElementsCollection;

            proposal = MsgParticipantServices.setFrameElements(proposal, 
							msgType, 
							proposal.receiver.getValue(), 
                            proposal.sender.getValue(),
							execution.getProcessInstanceId(),
							"decisionReceived",
							proposal.conversationId.getValue(),
							proposal.replyBy.getValue(),
							proposal.semanticProtocol.getValue(), 
							"ServiceRequester");            
        }      
    }
}
