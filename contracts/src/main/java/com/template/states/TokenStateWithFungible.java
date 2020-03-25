package com.template.states;

import net.corda.core.contracts.Amount;
import net.corda.core.contracts.FungibleState;
import net.corda.core.identity.AbstractParty;

import java.util.Currency;
import java.util.List;

public class TokenStateWithFungible implements FungibleState<Currency> {

    private Amount<Currency> amount;
    private String name;



    @Override
    public Amount getAmount() {
        return amount;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return null;
    }
}
