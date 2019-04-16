package com.agrostar.wallet.controller;

import com.agrostar.wallet.entity.Transactions;
import com.agrostar.wallet.enums.ResponseStatus;
import com.agrostar.wallet.exceptions.NoResourceFoundException;
import com.agrostar.wallet.exceptions.OperationNotAllowedException;
import com.agrostar.wallet.request.TransactionRequest;
import com.agrostar.wallet.response.GenericResponse;
import com.agrostar.wallet.response.PassbookResponseData;
import com.agrostar.wallet.service.TransactionService;
import com.agrostar.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletService walletService;

    private final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    @RequestMapping(method = RequestMethod.POST, path = "/wallet/{walletId}/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<String>> createTransaction(@RequestBody @Valid TransactionRequest transaction,
                                            @PathVariable int walletId) {
        LOGGER.debug("Processing transaction request for wallet {} with payload {}", walletId, transaction.toString());
        GenericResponse<String> response = new GenericResponse<>();
        try {
            Transactions transactionEntity = transactionService.createTransaction(walletId, transaction);
            response.setData(transactionEntity.getId());
            response.setMessage("Your transaction was successful");
            LOGGER.info("Finished processing transaction request for wallet {} with payload {}", walletId, transaction.toString());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            LOGGER.error("Error performing transaction on wallet {}. Error : {}", walletId, e.getMessage());
            response.setStatus(ResponseStatus.FAILED);
            response.setMessage("Failed to process your request, " + e.getMessage() + ", Because of " + e.getCause());
        }
        return ResponseEntity.badRequest().body(response);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/wallet/{walletId}/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<List<PassbookResponseData>>> getPassbook(@PathVariable int walletId) {
        LOGGER.debug("Fetching passbook data for wallet {}", walletId);
        GenericResponse<List<PassbookResponseData>> response = new GenericResponse<>();
        try {
            response.setData(transactionService.getAllTransactionByWallet(walletId));
            response.setMessage("Request processed successfully");
            LOGGER.info("Finished fetching passbook data for wallet {} with response {}", walletId, response);
            return ResponseEntity.ok(response);
        } catch (NoResourceFoundException | OperationNotAllowedException e) {
            LOGGER.error("Error getting passbook data for wallet {}. Error : {}", walletId, e.getMessage());
            response.setStatus(ResponseStatus.FAILED);
            response.setMessage("Failed to get passbook, " + e.getMessage());
        }
        return ResponseEntity.badRequest().body(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/wallet/{walletId}/transaction/{transactionId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<String>> cancelTransaction(@PathVariable int walletId, @PathVariable String transactionId) {
        GenericResponse<String> response = new GenericResponse<>(transactionId);
        try {
            if (transactionService.cancelTransaction(transactionId, walletId)) {
                response.setMessage("Transaction cancelled successfully");
                LOGGER.info("Successfully cancelled transaction {}", transactionId);
                return ResponseEntity.ok(response);
            }
            response.setStatus(com.agrostar.wallet.enums.ResponseStatus.FAILED);
            response.setMessage("Error occurred while cancelling transaction");
        } catch (OperationNotAllowedException | NoResourceFoundException e) {
            response.setStatus(ResponseStatus.FAILED);
            response.setMessage(e.getMessage());
        }
        LOGGER.error("Error cancelling transaction {}. Error : {}", transactionId, response.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/wallet/{walletId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<Double>> getWalletBalance(@PathVariable int walletId) {
        GenericResponse<Double> response = new GenericResponse<>();
        try {
            Double amount = walletService.getWalletBalance(walletId);
            response.setData(amount);
            return ResponseEntity.ok(response);
        } catch (NoResourceFoundException e) {
            LOGGER.error("Either Wallet is not available or deactivated for id {}", walletId);
            response.setStatus(ResponseStatus.FAILED);
            response.setMessage(e.getMessage());
        }
        return ResponseEntity.badRequest().body(response);
    }

}
