package com.template.schema;

import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.serialization.CordaSerializable;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@CordaSerializable
public class TokenSchemaV1 extends MappedSchema {
    public TokenSchemaV1() {
        super(TokenSchema.class, 1, ImmutableList.of(PersistentToken.class));
    }

    @Entity
    @Table(name="token_states")
    public static class PersistentToken extends PersistentState {


        @Column(name="issuer")
        private final String issuer;
        @Column(name="owner")
        private final String owner;
        @Column(name="amount")
        private final int amount;
        @Column(name="tokenDate")
        private final LocalDate tokenDate;
        @Column(name="expiredStatus")
        private final Boolean expiredStatus;

        public PersistentToken(String issuer, String owner, int amount, LocalDate tokenDate, Boolean expiredStatus, UUID linearId) {
            this.issuer = issuer;
            this.owner = owner;
            this.amount = amount;
            this.tokenDate = tokenDate;
            this.expiredStatus = expiredStatus;
            this.linearId = linearId;
        }

        public PersistentToken(){
            this.issuer = null;
            this.owner = null;
            this.amount = 0;
            this.tokenDate = null;
            this.expiredStatus = null;
            this.linearId = null;
        }
        @Column(name="linearId")
        private final UUID linearId;

        public String getIssuer() {
            return issuer;
        }

        public String getOwner() {
            return owner;
        }

        public int getAmount() {
            return amount;
        }

        public LocalDate getTokenDate() {
            return tokenDate;
        }

        public Boolean getExpiredStatus() {
            return expiredStatus;
        }

        public UUID getLinearId() {
            return linearId;
        }
    }
}
