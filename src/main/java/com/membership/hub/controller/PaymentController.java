package com.membership.hub.controller;

import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.model.shared.PaymentModel;
import com.membership.hub.security.Credentials;
import com.membership.hub.service.BranchService;
import com.membership.hub.service.PaymentService;
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
public class PaymentController {
    private final BranchService branchService;
    private final PaymentService paymentService;
    @Value("${administrator.password}")
    private String adminPassword;

    public PaymentController(BranchService branchService, PaymentService paymentService) {
        this.branchService = branchService;
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Void> sendPayment(
            @RequestHeader(AUTHORIZATION) String header,
            @RequestParam String branchId,
            @RequestParam Double amount,
            @RequestBody String description
    ) {
        Optional<BranchModel> existingBranch = branchService.getBranch(branchId);
        if(existingBranch.isPresent()) {
            String adminIdOfBranch = existingBranch.get().getAdminId();
            Credentials credentials = new Credentials(header);
            if (adminIdOfBranch.equals(credentials.getId()) && adminPassword.equals(credentials.getPassword())) {
                PaymentModel transaction = new PaymentModel(branchId, null, amount, LocalDate.now(), description);
                paymentService.sendPayment(transaction);
                return ResponseEntity.status(HttpStatus.ACCEPTED).build();
            }
            else {
                // TODO: throw Exception Bad Credentials
                System.out.println("Bad Credentials");
            }
        }
        // TODO: Exception that handles Branch Not Exist -> payment cannot be proceed
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<PaymentModel>> getAllPaymentsByBranch(@PathVariable String id) {
        List<PaymentModel> payments = paymentService.getAllPaymentsByBranch(id);

        if (payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.ok(payments);
        }
    }
}
