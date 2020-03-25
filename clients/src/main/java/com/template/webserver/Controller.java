package com.template.webserver;

import com.template.dto.RequestDto;
import com.token.flows.TokenIssueInitiator;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @GetMapping(value = "/templateendpoint", produces = "text/plain")
    private String templateendpoint() {
        return "Define an endpoint here.";
    }

    @PostMapping(value="/tokenGenerate" , produces= MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> tokenGnerate(@RequestBody RequestDto requestDto){

        Integer tokenValue=requestDto.getAmountValue();
        String partyName=requestDto.getPartyName();

        if(partyName == null){
            return ResponseEntity.badRequest().body("Query parameter 'partyName' must not be null.\n");
        }
        if (tokenValue <= 0 ) {
            return ResponseEntity.badRequest().body("Query parameter 'iouValue' must be non-negative.\n");
        }
        CordaX500Name partyX500Name = CordaX500Name.parse(partyName);
        Party otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name) ;
        if(! (otherParty instanceof Party)){
            return ResponseEntity.badRequest().body("Party named $partyName cannot be found.\n");
        }
        try {
            SignedTransaction signedTx = proxy.startTrackedFlowDynamic(TokenIssueInitiator.class,otherParty,tokenValue).getReturnValue().get();
         System.out.println("Transaction Done"+otherParty);
         logger.info(signedTx.getTx().getCommands().get(0).toString());
          return  ResponseEntity.status(HttpStatus.CREATED).body("Transaction id ${signedTx.id} committed to ledger.\n");

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
           return ResponseEntity.badRequest().body(ex.getMessage());
        }


    }
}