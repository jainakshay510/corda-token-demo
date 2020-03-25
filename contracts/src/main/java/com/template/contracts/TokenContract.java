package com.template.contracts;

import com.template.states.InterestRateMetadata;
import com.template.states.TokenState;
import net.corda.core.contracts.*;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

// ************
// * Contract *
// ************
public class TokenContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.TokenContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {

        CommandWithParties<CommandData> command = tx.getCommands().get(0);
        if (command.getValue() instanceof Commands.Issue){
            System.out.println("AKSHAY CONTRACT");
            if(tx.getInputStates().size() != 0)
                throw new IllegalArgumentException("Issuing token so no input state");
            if(tx.getOutputStates().size() != 1)throw new IllegalArgumentException("Owner of token should be a single entity");
            if(tx.getCommands().size() != 2 && tx.getCommands().size() != 1)throw new IllegalArgumentException("Transaction must have only two commands");
            ContractState outputState=tx.getOutput(0);
            Command commandI=tx.getCommand(0);
            if(!(outputState instanceof TokenState))throw new IllegalArgumentException("output must be a token state");
            TokenState tokenOutputState=(TokenState)outputState;
            if(tokenOutputState.getAmount() <= 0)throw new IllegalArgumentException("Amount should be a positive value");
            List<PublicKey> requiredSigners=commandI.getSigners();
            if(!(requiredSigners.contains(tokenOutputState.getIssuer().getOwningKey())))throw new IllegalArgumentException("Issuer must be a signing party");
        }else if (command.getValue() instanceof Commands.Transfer){
            if(tx.getInputStates().size() != 1)
                throw new IllegalArgumentException("Transfering token so single input state required");
            if(tx.getOutputStates().size() != 1)throw new IllegalArgumentException("Owner of token should be a single entity");
           if(tx.getCommands().size() != 2 && tx.getCommands().size() != 1)throw new IllegalArgumentException("Transaction must have only two commands");
            ContractState outputState=tx.getOutput(0);
            ContractState inputState=tx.getInput(0);
            Command commandI=tx.getCommand(0);
            if(!(outputState instanceof TokenState))throw new IllegalArgumentException("output must be a token state");
            TokenState tokenOutputState=(TokenState)outputState;
            if(tokenOutputState.getAmount() <= 0)throw new IllegalArgumentException("Amount should be a positive value");
            TokenState tokenInputState=(TokenState)inputState;
            if(tokenInputState.isExpired()==true)throw new IllegalArgumentException("Token is Expired");
            List<PublicKey> requiredSigners=commandI.getSigners();
            if(!(requiredSigners.contains(tokenInputState.getOwner().getOwningKey())))throw new IllegalArgumentException("payee must be a signing party");
            if(!(requiredSigners.contains(tokenOutputState.getOwner().getOwningKey())))throw new IllegalArgumentException("receiver must be a signing party");
        }else if (command.getValue() instanceof Commands.Redeem){
            if(tx.getInputStates().size()!=1)throw new IllegalArgumentException("Only one redeemer");
            if(tx.getOutputStates().size()!=0)throw new IllegalArgumentException("No output state if token redeemed");
            if(tx.getCommands().size() != 2 && tx.getCommands().size() != 1)throw new IllegalArgumentException("Transaction must have two one commands");
            ContractState inputState=tx.getInput(0);
            Command commandI=tx.getCommand(0);
            TokenState tokenInputState=(TokenState)inputState;
            if(tokenInputState.isExpired()==true)throw new IllegalArgumentException("Token is Expired");
            List<PublicKey> requiredSigners=commandI.getSigners();
            if(!(requiredSigners.contains(tokenInputState.getOwner().getOwningKey())))throw new IllegalArgumentException("payee must be a signing party");
        }
        else throw new IllegalArgumentException("Unrecognised command.");



    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class Issue implements Commands {}
        class Transfer implements Commands{}
        class Redeem implements  Commands{}
        class InterestRateDto implements Commands{
            private InterestRateMetadata interestRateMetadata;
            private int rate;

            public InterestRateMetadata getInterestRateMetadata() {
                return interestRateMetadata;
            }

            public void setInterestRateMetadata(InterestRateMetadata interestRateMetadata) {
                this.interestRateMetadata = interestRateMetadata;
            }

            @Override
            public String toString() {
                return "InterestRateDto{" +
                        "interestRateMetadata=" + interestRateMetadata +
                        ", rate=" + rate +
                        '}';
            }

            public int getRate() {
                return rate;
            }

            public InterestRateDto(InterestRateMetadata interestRateMetadata, int rate) {
                this.interestRateMetadata = interestRateMetadata;
                this.rate = rate;
            }

            public void setRate(int rate) {
                this.rate = rate;
            }
        }
    }
}