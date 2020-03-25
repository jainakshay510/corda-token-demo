package com.token.flows;


import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.TokenContract;
import com.template.states.InterestRateMetadata;
import com.token.api.Oracle;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;

@InitiatedBy(InterestRateQuery.class)
public class InterestRateQueryHandler extends FlowLogic<Void> {

    private FlowSession session;

    public InterestRateQueryHandler(FlowSession session) {
        this.session = session;
    }

    @Override
    @Suspendable
    public Void call() throws FlowException {
        InterestRateMetadata interestRateMetadata=session.receive(InterestRateMetadata.class).unwrap(it->it);

        TokenContract.Commands.InterestRateDto interestRateDto;
        try{
            interestRateDto=getServiceHub().cordaService(Oracle.class).query(interestRateMetadata);}
        catch(Exception ex){
            throw new FlowException(ex);
        }


        session.send(interestRateDto);

        return null;
    }
}
