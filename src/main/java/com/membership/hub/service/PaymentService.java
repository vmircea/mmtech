package com.membership.hub.service;

import com.membership.hub.exception.BranchException;
import com.membership.hub.exception.PaymentException;
import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.model.shared.PaymentModel;
import com.membership.hub.repository.BranchRepository;
import com.membership.hub.repository.PaymentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {
    private final PaymentsRepository paymentsRepository;
    private final BranchRepository branchRepository;

    public PaymentService(PaymentsRepository paymentsRepository, BranchRepository branchRepository) {
        this.paymentsRepository = paymentsRepository;
        this.branchRepository = branchRepository;
    }

    @Transactional
    public boolean sendPayment(PaymentModel transaction, BranchModel existingBranch) {
        Double branchAmount = existingBranch.getBranchAmount();
        // If SenderBranch Does Not Have Enough Money To Pay
        if (branchAmount < transaction.getAmount()) {
            throw PaymentException.branchHasNotEnoughMoneyForTransaction();
        }

        // If Receiver is null => Just decrease the total amount of the branch
        if (transaction.getReceiverId() == null) {
            Double negativeAmount = 0 - transaction.getAmount();
            if(branchRepository.updateAmount(transaction.getSenderId(), negativeAmount)) {
                if (paymentsRepository.save(transaction) != null) {
                    return true;
                }
                else {
                    throw PaymentException.paymentCouldNotBeProcessed();
                }
            }
            else {
                throw BranchException.senderBranchAmountNotUpdated();
            }
        }
        // Else if receiverId is not null => Transfer of money between branches
        else if (transaction.getReceiverId().length() == 11) {
            if (branchRepository.updateAmount(transaction.getSenderId(), 0 - transaction.getAmount())) {
                if (branchRepository.updateAmount(transaction.getReceiverId(), transaction.getAmount())) {
                    if (paymentsRepository.save(transaction) != null) {
                        return true;
                    }
                    else {
                        throw PaymentException.paymentCouldNotBeProcessed();
                    }
                }
                else {
                    throw BranchException.receiverBranchAmountNotUpdated();
                }
            }
            else {
                throw BranchException.senderBranchAmountNotUpdated();
            }
        }
        else {
            throw BranchException.wrongBranchIdType();
        }
    }


    public List<PaymentModel> getAllPaymentsByBranch(String id) {
        return paymentsRepository.findAllById(id);
    }
}
