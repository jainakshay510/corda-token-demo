package com.template.dto;

import net.corda.core.identity.Party;


public class RequestDto {
    private String partyName;
    private int amountValue;
    
    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void setAmountValue(int amountValue) {
        this.amountValue = amountValue;
    }

    public String getPartyName() {
        return partyName;
    }

    public int getAmountValue() {
        return amountValue;
    }
}
