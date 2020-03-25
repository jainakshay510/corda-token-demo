package com.token.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.template.contracts.TokenContract;
import com.template.contracts.TokenContract.Commands;
import com.template.states.TokenState;
import com.token.api.InterestRate;
import com.template.states.InterestRateMetadata;
import net.corda.core.contracts.Command;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;


import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singletonList;

// ******************
// * TokenIssueInitiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class TokenIssueInitiator extends FlowLogic<SignedTransaction> {

    private final Party owner;
    private final int amount;

    public TokenIssueInitiator(Party owner, int amount) {
        this.owner = owner;
        this.amount = amount;
    }



    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // We choose our transaction's notary (the notary prevents double-spends).
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        //We get an Oracle Service
        Party oracle=getServiceHub().getNetworkMapCache().getPeerByLegalName(new CordaX500Name("Oracle", "London", "GB"));
        // We get a reference to our own identity.
        Party issuer = getOurIdentity();


        InterestRateMetadata interestRateMetadata=new InterestRateMetadata("Monthly",LocalDate.now());
        //InterestRateDto interestRateDto=subFlow(new InterestRateQuery(interestRateMetadata,oracle));

        // We create our new TokenState.
        TokenState tokenState = new TokenState(issuer,owner,amount,LocalDate.now(),false);
        Commands.Issue command=new Commands.Issue();

        // We build our transaction.
        TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
        transactionBuilder.addOutputState(tokenState,TokenContract.ID);

        Commands.InterestRateDto interestRateDto=subFlow(new InterestRateQuery(interestRateMetadata,oracle));
        getLogger().info("Akshay IntersetRateDto---------------------------"+interestRateDto.getClass());


        //why we have to add the assigning party signature with whom we are starting the session
        transactionBuilder.addCommand(command, ImmutableList.of(issuer.getOwningKey(),owner.getOwningKey()));
        transactionBuilder.addCommand(interestRateDto,oracle.getOwningKey());


          getLogger().info("Command1::::::"+transactionBuilder.commands().get(0));
        getLogger().info("Command1::::::"+transactionBuilder.commands().get(1));

        // We check our transaction is valid based on its contracts.
        transactionBuilder.verify(getServiceHub());



        FlowSession session = initiateFlow(owner);


        // We sign the transaction with our private key, making it immutable.

        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        TransactionSignature transactionSignature=subFlow(new InterestRate(oracle,interestRateMetadata,signedTransaction));
        //SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);
        signedTransaction=signedTransaction.withAdditionalSignature(transactionSignature);

       List<Command<?>> listcmd1=signedTransaction.getTx().getCommands();
       for(Command c : listcmd1){
           getLogger().info("Signed Transaction Commands ::::::"+command);
       }



//        TransactionSignature transactionSignature = subFlow(new CollectSignatureFlow(signedTransaction, session, owner.getOwningKey())).get(0);
//        signedTransaction=signedTransaction.withAdditionalSignature(transactionSignature);

        // The counterparty signs the transaction
        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, singletonList(session)));

        // We get the transaction notarised and recorded automatically by the platform.
        return subFlow(new FinalityFlow(fullySignedTransaction, singletonList(session)));
    }
}
