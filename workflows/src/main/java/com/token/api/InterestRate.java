package com.token.api;


import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.TokenContract;
import com.template.states.InterestRateMetadata;
import com.token.flows.InterserRateSign;
import net.corda.core.contracts.Command;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.identity.Party;
import net.corda.core.transactions.FilteredTransaction;
import net.corda.core.transactions.SignedTransaction;

public class InterestRate extends FlowLogic<TransactionSignature> {

    private Party oracle;
    private InterestRateMetadata interestRateMetadata;
    private SignedTransaction signedTransaction;

    public InterestRate(Party oracle, InterestRateMetadata interestRateMetadata, SignedTransaction signedTransaction) {
        this.oracle = oracle;
        this.interestRateMetadata = interestRateMetadata;
        this.signedTransaction = signedTransaction;
    }

    @Suspendable
    public TransactionSignature call() throws FlowException {


//        InterestRateDto interestRateDto=subFlow(new InterestRateQuery(interestRateMetadata,oracle));
//        getLogger().info("Akshay IntersetRateDto---------------------------"+interestRateDto);
//        transactionBuilder.addCommand(interestRateDto,oracle.getOwningKey());
        //SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        for(Command c:signedTransaction.getTx().getCommands()){
            getLogger().info("Inside InterestRate command :::::::::"+c);
        }
        FilteredTransaction filteredTransaction=signedTransaction.buildFilteredTransaction(it->{if(it instanceof Command && ((Command) it).getSigners().contains(oracle.getOwningKey()) && ((Command) it).getValue() instanceof TokenContract.Commands.InterestRateDto)return true;
        else return false;});
        getLogger().info("Akshay FilteredTransaction--------------------------------------"+filteredTransaction.toString());
        TransactionSignature transactionSignature=subFlow(new InterserRateSign(oracle,filteredTransaction));


        return transactionSignature;
    }
}
