package com.token.api;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.TokenContract;
import com.template.states.InterestRateMetadata;
import net.corda.core.contracts.Command;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.node.AppServiceHub;
import net.corda.core.node.services.CordaService;
import net.corda.core.serialization.SingletonSerializeAsToken;
import net.corda.core.transactions.FilteredTransaction;
import net.corda.core.transactions.FilteredTransactionVerificationException;

import java.security.PublicKey;

@CordaService
public class Oracle extends SingletonSerializeAsToken{

    private AppServiceHub serviceHub;
    private PublicKey myKey;
    public Oracle(AppServiceHub serviceHub) {
        super();
        this.serviceHub = serviceHub;
    }





    public TokenContract.Commands.InterestRateDto query(InterestRateMetadata interestRateMetadata){

        return new TokenContract.Commands.InterestRateDto(interestRateMetadata,20);

    }


    public TransactionSignature sign(FilteredTransaction ftx) throws FilteredTransactionVerificationException {
       this.myKey=serviceHub.getMyInfo().getLegalIdentities().get(0).getOwningKey();
        ftx.verify();
       boolean isValidMerkleTree=ftx.checkWithFun(it->{if(it instanceof Command && ((Command) it).getValue() instanceof TokenContract.Commands.InterestRateDto){
            TokenContract.Commands.InterestRateDto dto= (TokenContract.Commands.InterestRateDto) ((Command) it).getValue();
            return ((Command) it).getSigners().contains(myKey) && (query(dto.getInterestRateMetadata()).getRate()==dto.getRate());
        }
        else return false;
        });

        if (isValidMerkleTree) {
            return serviceHub.createSignature(ftx,serviceHub.getMyInfo().getLegalIdentities().get(0).getOwningKey());
        } else {
            throw new IllegalArgumentException("Oracle signature requested over invalid transaction.");
        }



    }




}
