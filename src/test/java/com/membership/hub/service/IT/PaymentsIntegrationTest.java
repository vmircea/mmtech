package com.membership.hub.service.IT;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@TestPropertySource(locations = "classpath:application-test.properties")
public class PaymentsIntegrationTest {
    @Autowired
    private PaymentService service;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private PaymentsRepository paymentsRepository;

    private static PaymentModel transactionForBills;
    private static PaymentModel transactionForBillsWrongBranchId;
    private static PaymentModel transactionForBillsToAnotherBranch;
    private static PaymentModel transactionToBranchWhichNotExists;
    private static PaymentModel transactionForBillsToAnotherWrongIdType;
    private static PaymentModel transactionFromBranchWhichNotExists;

    private static List<PaymentModel> branchTransactions;
    private static BranchModel branchNoMoney;
    private static BranchModel branchWithMoney;
    private static BranchModel branchWithWrongId;
    private static BranchModel branchToTransferMoney;

    @BeforeAll
    public static void setup() {
        transactionForBills = new PaymentModel(
                "XX-TTT-0000",
                null,
                120.0,
                LocalDate.now(),
                "Bill Payment"
        );
        transactionForBillsWrongBranchId = new PaymentModel(
                "YY-TTT-0000",
                null,
                120.0,
                LocalDate.now(),
                "Bill Payment"
        );
        transactionForBillsToAnotherBranch = new PaymentModel(
                "XX-TTT-0000",
                "ZZ-TTT-0000",
                120.0,
                LocalDate.now(),
                "Bill Payment"
        );
        transactionToBranchWhichNotExists = new PaymentModel(
                "XX-TTT-0000",
                "WW-TTT-0000",
                120.0,
                LocalDate.now(),
                "Bill Payment"
        );
        transactionFromBranchWhichNotExists = new PaymentModel(
                "PP-TTT-0000",
                "WW-TTT-0000",
                120.0,
                LocalDate.now(),
                "Bill Payment"
        );
        transactionForBillsToAnotherWrongIdType = new PaymentModel(
                "XX-TTT-0000",
                "YY-TTT-00009999999999",
                100.0,
                LocalDate.now(),
                "Transfer from XX to YY"
        );
        ContactInfo contactAddedId = new ContactInfo(
                1,
                "+40700999000",
                "new@test.com",
                "CountryTest",
                "CityTest",
                "StreetTest",
                "BuildingTest");
        branchNoMoney = new BranchModel(
                "XX-TTT-0000",
                "4e48d7a8-64b9-4455-a37f-903fd62def32",
                "Test Name No 3",
                contactAddedId,
                0.0
        );
        branchWithMoney = new BranchModel(
                "XX-TTT-0000",
                "4e48d7a8-64b9-4455-a37f-903fd62def32",
                "Test Name No 3",
                contactAddedId,
                150.0
        );
        branchWithWrongId = new BranchModel(
                "YY-TTT-0000",
                "4e48d7a8-64b9-4455-a37f-903fd62def32",
                "Test Name No 3",
                contactAddedId,
                150.0
        );
    }

    /**
     * Case Not Enough Money From Sender Branch:
     */

    @Test
    public void sendPaymentNotEnoughMoney() {
        branchRepository.updateAmount(
                "XX-TTT-0000",
                0.0);

        PaymentException exception = assertThrows(
                PaymentException.class,
                () -> service.sendPayment(transactionForBills, branchNoMoney)
        );

        assertEquals(PaymentException.PaymentErrors.BRANCH_HAS_NOT_ENOUGH_MONEY_FOR_TRANSACTION,
                exception.getError());
    }

    /**
     * Case Branch To Null:
     */

    @Test
    public void sendPaymentEnoughMoneyWrongBranchId() {
        BranchException exception = assertThrows(
                BranchException.class,
                () -> service.sendPayment(transactionForBillsWrongBranchId, branchWithWrongId)
        );

        assertEquals(BranchException.BranchErrors.SENDER_BRANCH_AMOUNT_NOT_UPDATED,
                exception.getError());
    }

    @Test
    public void sendPaymentEnoughMoneyNoReceiverHappyFlow() {
        Boolean result = service.sendPayment(transactionForBills, branchWithMoney);

        assertTrue(result);
    }

    /**
     * Case Branch To Branch:
     */

    @Test
    public void sendPaymentFromBranchToBranchWithWrongIdType() {
        BranchException exception = assertThrows(
                BranchException.class,
                () -> service.sendPayment(transactionForBillsToAnotherWrongIdType, branchWithMoney)
        );
        assertEquals(BranchException.BranchErrors.WRONG_BRANCH_ID_TYPE,
                exception.getError());
    }

    @Test
    public void sendPaymentFromBranchWhichNotExistsToOtherBranch() {
        BranchException exception = assertThrows(
                BranchException.class,
                () -> service.sendPayment(transactionFromBranchWhichNotExists, branchWithMoney)
        );

        assertEquals(BranchException.BranchErrors.SENDER_BRANCH_AMOUNT_NOT_UPDATED,
                exception.getError());
    }

    @Test
    public void sendPaymentFromBranchToOtherBranchWhichNotExists() {
        BranchException exception = assertThrows(
                BranchException.class,
                () -> service.sendPayment(transactionToBranchWhichNotExists, branchWithMoney)
        );

        assertEquals(BranchException.BranchErrors.RECEIVER_BRANCH_AMOUNT_NOT_UPDATED,
                exception.getError());
    }

    @Test
    public void sendPaymentFromBranchWithEnoughMoneyToAnotherBranchHappyFlow() {
        Boolean result = service.sendPayment(transactionForBillsToAnotherBranch, branchWithMoney);

        assertTrue(result);
    }


}
