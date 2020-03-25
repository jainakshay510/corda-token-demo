package com.token.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.token.api.Oracle;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.transactions.FilteredTransaction;

@InitiatedBy(InterserRateSign.class)
public class IntersetRateSignHandler extends FlowLogic<Void> {

    private FlowSession flowSession;

    public IntersetRateSignHandler(FlowSession flowSession) {
        this.flowSession = flowSession;
    }

    @Suspendable
    public Void call() throws FlowException {

        FilteredTransaction filteredTransaction=flowSession.receive(FilteredTransaction.class).unwrap(it->it);


        TransactionSignature transactionSignature;

        try{
            transactionSignature=getServiceHub().cordaService(Oracle.class).sign(filteredTransaction);
        }catch(Exception ex){
            throw new FlowException(ex);
        }


        flowSession.send(transactionSignature);
        return null;
    }
}
