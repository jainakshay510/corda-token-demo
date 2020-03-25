package com.token.flows;


import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.TokenContract;
import com.template.states.InterestRateMetadata;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.identity.Party;
import net.corda.core.utilities.UntrustworthyData;

@InitiatingFlow
public class InterestRateQuery extends FlowLogic<TokenContract.Commands.InterestRateDto>{

    private InterestRateMetadata interestRateMetadata;
    private Party oracle;

    public InterestRateQuery(InterestRateMetadata interestRateMetadata, Party oracle) {
        this.interestRateMetadata = interestRateMetadata;
        this.oracle = oracle;
    }

    @Override
    @Suspendable
    public TokenContract.Commands.InterestRateDto call() throws FlowException {

       FlowSession oracleSession=initiateFlow(oracle);
       UntrustworthyData<TokenContract.Commands.InterestRateDto> interestRateDto=oracleSession.sendAndReceive(TokenContract.Commands.InterestRateDto.class,interestRateMetadata);


       getLogger().info("Interestrate Metadata -------------------"+interestRateMetadata);
        TokenContract.Commands.InterestRateDto rateDto = interestRateDto.unwrap(
                it -> {
                    getLogger().info("it interestRateMetadata ------------------------------"+it.getInterestRateMetadata());
                    if(it.getInterestRateMetadata().getName().equals(interestRateMetadata.getName()))return it;
                    else throw new IllegalArgumentException("Data is not of requested type");
                }

        );
        return rateDto;
    }
}
