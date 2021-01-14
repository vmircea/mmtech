package com.membership.hub.mapper;

import com.membership.hub.model.shared.PaymentModel;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PaymentModelFactory {
    public PaymentModel createPaymentModel(String senderId, String receiverId, Double amount, LocalDate date, String description) {
        return new PaymentModel(senderId, receiverId, amount, date, description);
    }
}
