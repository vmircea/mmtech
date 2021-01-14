package com.membership.hub.service.unit;

import com.membership.hub.exception.BranchException;
import com.membership.hub.exception.PaymentException;
import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.model.shared.ContactInfo;
import com.membership.hub.model.shared.PaymentModel;
import com.membership.hub.repository.BranchRepository;
import com.membership.hub.repository.PaymentsRepository;
import com.membership.hub.service.PaymentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private PaymentsRepository paymentsRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private PaymentService service;

    private static PaymentModel transactionForBills;
    private static PaymentModel transactionForBillsToAnotherBranch;
    private static PaymentModel transactionForBillsToAnotherWrongIdType;
    private static List<PaymentModel> branchTransactions;
    private static BranchModel branchFoundNoMoney;
    private static BranchModel branchFoundWithMoney;

    @BeforeAll
    public static void setup() {
        transactionForBills = new PaymentModel(
                "XX-TTT-0000",
                null,
                100.0,
                LocalDate.now(),
                "Bill Payment"
        );
        transactionForBillsToAnotherBranch = new PaymentModel(
                "XX-TTT-0000",
                "YY-TTT-0000",
                100.0,
                LocalDate.now(),
                "Transfer from XX to YY"
        );
        transactionForBillsToAnotherWrongIdType = new PaymentModel(
                "XX-TTT-0000",
                "YY-TTT-00009999999999",
                100.0,
                LocalDate.now(),
                "Transfer from XX to YY"
        );
        branchTransactions = new ArrayList<>();
        branchTransactions.add(transactionForBills);
        branchTransactions.add(transactionForBillsToAnotherBranch);

        ContactInfo contactAddedId = new ContactInfo(
                1,
                "+40700999000",
                "new@test.com",
                "CountryTest",
                "CityTest",
                "StreetTest",
                "BuildingTest");
        branchFoundNoMoney = new BranchModel(
                "XX-TTT-0000",
                "4e48d7a8-64b9-4455-a37f-903fd62def32",
                "Test Name No 3",
                contactAddedId,
                0.0
        );
        branchFoundWithMoney = new BranchModel(
                "XX-TTT-0000",
                "4e48d7a8-64b9-4455-a37f-903fd62def32",
                "Test Name No 3",
                contactAddedId,
                200.0
        );
    }

    @Test
    public void sendPaymentNotEnoughMoney() {
        PaymentException exception = assertThrows(
                PaymentException.class,
                () -> service.sendPayment(transactionForBills, branchFoundNoMoney)
        );
        assertEquals(PaymentException.PaymentErrors.BRANCH_HAS_NOT_ENOUGH_MONEY_FOR_TRANSACTION,
                exception.getError());
    }

    @Test
    public void sendPaymentNoReceiverHappyFlow() {
        when(paymentsRepository.save(transactionForBills))
                .thenReturn(transactionForBills);
        when(branchRepository.updateAmount(
                transactionForBills.getSenderId(),
                0 - transactionForBills.getAmount()
        )).thenReturn(true);

        boolean result = service.sendPayment(transactionForBills, branchFoundWithMoney);

        assertTrue(result);
    }

    @Test
    public void sendPaymentFromBranchToNullWhenAmountNotUpdated() {
        when(branchRepository.updateAmount(
                transactionForBills.getSenderId(),
                0 - transactionForBills.getAmount()
        )).thenReturn(false);

        BranchException exception = assertThrows(
                BranchException.class,
                () -> service.sendPayment(transactionForBills, branchFoundWithMoney)
        );
        assertEquals(BranchException.BranchErrors.SENDER_BRANCH_AMOUNT_NOT_UPDATED,
                exception.getError());
    }

    @Test
    public void sendPaymentFromBranchToNullWhenPaymentNotSaved() {
        when(branchRepository.updateAmount(
                transactionForBills.getSenderId(),
                0 - transactionForBills.getAmount()
        )).thenReturn(true);
        when(paymentsRepository.save(transactionForBills))
                .thenReturn(null);


        PaymentException exception = assertThrows(
                PaymentException.class,
                () -> service.sendPayment(transactionForBills, branchFoundWithMoney)
        );
        assertEquals(PaymentException.PaymentErrors.PAYMENT_COULD_NOT_BE_PROCESSED,
                exception.getError());
    }

    @Test
    public void sendPaymentToAnotherBranchHappyFlow() {
        when(branchRepository.updateAmount(
                transactionForBillsToAnotherBranch.getSenderId(),
                0 - transactionForBillsToAnotherBranch.getAmount()
        )).thenReturn(true);
        when(branchRepository.updateAmount(
                transactionForBillsToAnotherBranch.getReceiverId(),
                transactionForBillsToAnotherBranch.getAmount()
        )).thenReturn(true);
        when(paymentsRepository.save(transactionForBillsToAnotherBranch))
                .thenReturn(transactionForBillsToAnotherBranch);

        boolean result = service.sendPayment(transactionForBillsToAnotherBranch, branchFoundWithMoney);

        assertTrue(result);
    }

    @Test
    public void sendPaymentToAnotherBranchWhenPaymentNotSaved() {
        when(branchRepository.updateAmount(
                transactionForBillsToAnotherBranch.getSenderId(),
                0 - transactionForBillsToAnotherBranch.getAmount()
        )).thenReturn(true);
        when(branchRepository.updateAmount(
                transactionForBillsToAnotherBranch.getReceiverId(),
                transactionForBillsToAnotherBranch.getAmount()
        )).thenReturn(true);
        when(paymentsRepository.save(transactionForBillsToAnotherBranch))
                .thenReturn(null);


        PaymentException exception = assertThrows(
                PaymentException.class,
                () -> service.sendPayment(transactionForBillsToAnotherBranch, branchFoundWithMoney)
        );
        assertEquals(PaymentException.PaymentErrors.PAYMENT_COULD_NOT_BE_PROCESSED,
                exception.getError());
    }

    @Test
    public void sendPaymentToAnotherBranchWhenReceiverBranchAmountNotUpdated() {
        when(branchRepository.updateAmount(
                transactionForBillsToAnotherBranch.getSenderId(),
                0 - transactionForBillsToAnotherBranch.getAmount()
        )).thenReturn(true);
        when(branchRepository.updateAmount(
                transactionForBillsToAnotherBranch.getReceiverId(),
                transactionForBillsToAnotherBranch.getAmount()
        )).thenReturn(false);

        BranchException exception = assertThrows(
                BranchException.class,
                () -> service.sendPayment(transactionForBillsToAnotherBranch, branchFoundWithMoney)
        );
        assertEquals(BranchException.BranchErrors.RECEIVER_BRANCH_AMOUNT_NOT_UPDATED,
                exception.getError());
    }

    @Test
    public void sendPaymentToAnotherBranchWhenSenderBranchAmountNotUpdated() {
        when(branchRepository.updateAmount(
                transactionForBillsToAnotherBranch.getSenderId(),
                0 - transactionForBillsToAnotherBranch.getAmount()
        )).thenReturn(false);

        BranchException exception = assertThrows(
                BranchException.class,
                () -> service.sendPayment(transactionForBillsToAnotherBranch, branchFoundWithMoney)
        );
        assertEquals(BranchException.BranchErrors.SENDER_BRANCH_AMOUNT_NOT_UPDATED,
                exception.getError());
    }

    @Test
    public void sendPaymentToAnotherBranchWhenReceiverIsNotBranch() {
        BranchException exception = assertThrows(
                BranchException.class,
                () -> service.sendPayment(transactionForBillsToAnotherWrongIdType, branchFoundWithMoney)
        );
        assertEquals(BranchException.BranchErrors.WRONG_BRANCH_ID_TYPE,
                exception.getError());
    }

    @Test
    public void getAllPaymentsByBranchTest() {
        when(paymentsRepository.findAllById(branchFoundWithMoney.getBranchId()))
                .thenReturn(branchTransactions);

        List<PaymentModel> result = service.getAllPaymentsByBranch(branchFoundWithMoney.getBranchId());

        assertEquals(branchTransactions.get(0), result.get(0));
        assertEquals(branchTransactions.get(1), result.get(1));
    }
}
