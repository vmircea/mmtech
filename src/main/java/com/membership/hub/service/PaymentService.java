package com.membership.hub.service;

import com.membership.hub.model.shared.PaymentModel;
import com.membership.hub.repository.BranchRepository;
import com.membership.hub.repository.PaymentsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    private final PaymentsRepository paymentsRepository;
    private final BranchRepository branchRepository;

    public PaymentService(PaymentsRepository paymentsRepository, BranchRepository branchRepository) {
        this.paymentsRepository = paymentsRepository;
        this.branchRepository = branchRepository;
    }

    public void sendPayment(PaymentModel transaction) {
        paymentsRepository.save(transaction);
        Double negativeAmount = 0 - transaction.getAmount();
        branchRepository.updateAmount(transaction.getSenderId(), negativeAmount);
    }

    public List<PaymentModel> getAllPaymentsByBranch(String id) {
        return paymentsRepository.findAllById(id);
    }
}
