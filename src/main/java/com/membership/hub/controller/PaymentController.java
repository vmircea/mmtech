package com.membership.hub.controller;

import com.membership.hub.exception.BranchException;
import com.membership.hub.exception.PaymentException;
import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.model.shared.PaymentModel;
import com.membership.hub.security.Credentials;
import com.membership.hub.service.BranchService;
import com.membership.hub.service.PaymentService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.membership.hub.security.Credentials.AUTHORIZATION;

@RestController
@Validated
@RequestMapping("/payment")
@Api(value = "/payment",
        tags = "Payments")
public class PaymentController {
    private final BranchService branchService;
    private final PaymentService paymentService;
    @Value("${administrator.password}")
    private String adminPassword;

    public PaymentController(BranchService branchService, PaymentService paymentService) {
        this.branchService = branchService;
        this.paymentService = paymentService;
    }

    @ApiOperation(value = "Send a new payment",
            notes = "Send a payment from a branch to: null, another branch (transfer), to a project")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The payment was successfully done based on the received request"),
            @ApiResponse(code = 401, message = "Payment cannot be proceed due to bad credentials"),
            @ApiResponse(code = 500, message = "Payment cannot be proceed"),
    })
    @PostMapping
    public ResponseEntity<Void> sendPayment(
            @RequestHeader(AUTHORIZATION) String header,
            @ApiParam(name = "payment", value = "Payment details and credentials", required = true)
            @RequestBody PaymentModel payment
    ) {
        Optional<BranchModel> existingBranch = branchService.getBranch(payment.getSenderId());
        if(existingBranch.isPresent()) {
            String adminIdOfBranch = existingBranch.get().getAdminId();
            Credentials credentials = new Credentials(header);
            if (adminIdOfBranch.equals(credentials.getId()) && adminPassword.equals(credentials.getPassword())) {
                LocalDate paymentDate;
                if (payment.getDate() == null) {
                     paymentDate = LocalDate.now();
                }
                else {
                    paymentDate = payment.getDate();
                }
                PaymentModel transaction = new PaymentModel(
                        payment.getSenderId(),
                        payment.getReceiverId(),
                        payment.getAmount(),
                        paymentDate,
                        payment.getDescription());
                if(paymentService.sendPayment(transaction, existingBranch.get())) {
                    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
                }
            }
            else {
                throw PaymentException.badCredentials();
            }
        }
        throw BranchException.branchNotFound();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<PaymentModel>> getAllPaymentsByBranch(@PathVariable String id) {
        Optional<BranchModel> existingBranch = branchService.getBranch(id);
        if (existingBranch.isPresent()) {
            List<PaymentModel> payments = paymentService.getAllPaymentsByBranch(id);

            if (payments.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            else {
                return ResponseEntity.ok(payments);
            }
        }
        else {
            throw BranchException.branchNotFound();
        }
    }
}
