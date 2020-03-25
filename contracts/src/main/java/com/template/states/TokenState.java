package com.template.states;

import com.google.common.collect.ImmutableList;
import com.template.contracts.TokenContract;
import com.template.schema.TokenSchemaV1;
import net.corda.core.contracts.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(TokenContract.class)
public class TokenState implements LinearState, QueryableState {

    private AbstractParty issuer;
    private AbstractParty owner;
    private LocalDate date;
    private Boolean expiredStatus;
    private int amount;


    public TokenState(AbstractParty issuer,AbstractParty owner,int amount,LocalDate date,Boolean expiredStatus){
        this.issuer=issuer;
        this.owner=owner;
        this.amount=amount;
        this.date=date;
        this.expiredStatus=expiredStatus;
    }

    public AbstractParty getIssuer() {
        return issuer;
    }

    public int getAmount() {
        return amount;
    }

    public AbstractParty getOwner() {
        return owner;
    }

    public Boolean isExpired(){ return expiredStatus;}

    public LocalDate getDate(){ return date;}

    public List<AbstractParty> getParticipants() {
        List<AbstractParty> party=new ArrayList<>();
        party.add(issuer);
        party.add(owner);
        return party;
    }


    private static UniqueIdentifier uniqueLinearId=new UniqueIdentifier("12345");

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return uniqueLinearId;
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        if (schema instanceof TokenSchemaV1) {
            return new TokenSchemaV1.PersistentToken(
                    this.issuer.nameOrNull().toString(),
                    this.owner.nameOrNull().toString(),
                    this.amount,
                    this.date,
                    this.expiredStatus,
                    this.getLinearId().getId());
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return  ImmutableList.of(new TokenSchemaV1());
    }
}