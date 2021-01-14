package com.membership.hub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.model.shared.ContactInfo;
import com.membership.hub.model.shared.PaymentModel;
import com.membership.hub.security.Credentials;
import com.membership.hub.service.BranchService;
import com.membership.hub.service.PaymentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = PaymentController.class)
@EnableTransactionManagement
public class PaymentControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BranchService branchService;
    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${administrator.password}")
    private String adminPassword;
    private static String wrongPassword;

    private static BranchModel branchAlreadyExists;
    private static PaymentModel transactionForBills;
    private static List<PaymentModel> paymentsList;
    private static List<PaymentModel> paymentsEmptyList;

    @BeforeAll
    public static void setup() {
        branchAlreadyExists = new BranchModel(
                "TT-XXX-0000",
                "4e48d7a8-64b9-4455-a37f-903fd62def32",
                "Test Name No 1",
                new ContactInfo(),
                200.0);
        transactionForBills = new PaymentModel(
                "TT-XXX-0000",
                null,
                100.0,
                LocalDate.now(),
                "Bill Payment"
        );
        paymentsList = new ArrayList<>();
        paymentsList.add(transactionForBills);
        paymentsEmptyList = new ArrayList<>();

        wrongPassword = "wrongPassword";
    }

    private static String makeCredentials(String id, String password) {
        return String.format("Basic %s#%s", id, password);
    }

    @Test
    public void sendPaymentHappyFlow() throws Exception {
        when(branchService.getBranch(any())).thenReturn(Optional.of(branchAlreadyExists));
        when(paymentService.sendPayment(any(), any()))
                .thenReturn(true);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/payment")
                        .header((Credentials.AUTHORIZATION), makeCredentials(
                                "4e48d7a8-64b9-4455-a37f-903fd62def32", adminPassword))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionForBills))
                ).andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    @Test
    public void sendPaymentBranchNotFound() throws Exception {
        when(branchService.getBranch(any())).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/payment")
                        .header((Credentials.AUTHORIZATION), makeCredentials(
                                "4e48d7a8-64b9-4455-a37f-903fd62def32", adminPassword))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionForBills))
        )
        .andExpect(MockMvcResultMatchers.status().isInternalServerError()).andReturn();

        assertEquals("Exception: BRANCH_NOT_FOUND", result.getResponse().getContentAsString());
    }

    @Test
    public void sendPaymentBadCredentials() throws Exception {
        when(branchService.getBranch(any())).thenReturn(Optional.of(branchAlreadyExists));

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/payment")
                        .header((Credentials.AUTHORIZATION), makeCredentials(
                                "4e48d7a8-64b9-4455-a37f-903fd62def32", wrongPassword))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionForBills))
        )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized()).andReturn();

        assertEquals("Exception: BAD_CREDENTIALS", result.getResponse().getContentAsString());
    }

    @Test
    public void getAllPaymentsByBranch() throws Exception {
        when(branchService.getBranch(any())).thenReturn(Optional.of(branchAlreadyExists));
        when(paymentService.getAllPaymentsByBranch(any()))
                .thenReturn(paymentsList);
        String endpoint = "/payment/%s";
        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format(endpoint, "TT-XXX-0000"))
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getAllPaymentsByBranchReturnEmptyList() throws Exception {
        when(branchService.getBranch(any())).thenReturn(Optional.of(branchAlreadyExists));
        when(paymentService.getAllPaymentsByBranch(any()))
                .thenReturn(paymentsEmptyList);
        String endpoint = "/payment/%s";
        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format(endpoint, "TT-XXX-0000"))
        ).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void getAllPaymentsByBranchWhichIsNotFound() throws Exception {
        when(branchService.getBranch(any())).thenReturn(Optional.empty());

        String endpoint = "/payment/%s";
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get(String.format(endpoint, "TT-XXX-0000"))
        ).andExpect(MockMvcResultMatchers.status().isInternalServerError()).andReturn();

        assertEquals("Exception: BRANCH_NOT_FOUND", result.getResponse().getContentAsString());
    }
}
