package com.membership.hub.service.unit;

import com.membership.hub.exception.BranchException;
import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.model.shared.ContactInfo;
import com.membership.hub.repository.BranchRepository;
import com.membership.hub.repository.shared.ContactRepository;
import com.membership.hub.service.BranchService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BranchServiceTest {

    @Mock
    private BranchRepository branchRepository;
    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private BranchService branchService;

    private static List<BranchModel> existingBranches;
    private static BranchModel newBranchAlreadyExists;
    private static BranchModel newBranchHappyFlow;
    private static BranchModel branchForComparingHappyFlow;

    @BeforeAll
    public static void setup() {
        existingBranches = new ArrayList<>();
        existingBranches.add(new BranchModel(
                "TT-XXX-0000",
                "4e48d7a8-64b9-4455-a37f-903fd62def32",
                "Test Name No 1",
                new ContactInfo(),
                0.0));
        existingBranches.add(new BranchModel(
                "CC-XXX-0000",
                "8D48d7a8-64b9-4455-a37f-903fd62def32",
                "Test Name No 2",
                new ContactInfo(),
                0.0
        ));

        newBranchAlreadyExists = new BranchModel(
                "TT-XXX-0000",
                "4e48d7a8-64b9-4455-a37f-903fd62def32",
                "Test Name No 1",
                new ContactInfo(),
                0.0);

        ContactInfo newContactHappyFlow = new ContactInfo(
                "+40700999000",
                "new@test.com",
                "CountryTest",
                "CityTest",
                "StreetTest",
                "BuildingTest");
        newBranchHappyFlow = new BranchModel(
                "AA-XXX-0000",
                "4e48d7a8-64b9-4455-a37f-903fd62def32",
                "Test Name No 3",
                newContactHappyFlow,
                0.0);
        ContactInfo contactAddedId = new ContactInfo(
                1,
                "+40700999000",
                "new@test.com",
                "CountryTest",
                "CityTest",
                "StreetTest",
                "BuildingTest");
        branchForComparingHappyFlow = new BranchModel(
                "AA-XXX-0000",
                "4e48d7a8-64b9-4455-a37f-903fd62def32",
                "Test Name No 3",
                contactAddedId,
                0.0);
    }

    @Test
    public void createNewBranchWhenAlreadyExists() {
        when(branchRepository.findAll()).thenReturn(existingBranches);
        BranchException exception = assertThrows(
                BranchException.class,
                () -> branchService.createNewBranch(newBranchAlreadyExists));
        assertEquals(BranchException.BranchErrors.BRANCH_ALREADY_EXISTS_WITH_ID, exception.getError());
    }

    @Test
    public void createNewBranchHappyFlow() {
        when(branchRepository.findAll()).thenReturn(existingBranches);
        when(contactRepository.save(newBranchHappyFlow.getContactInfo())).thenReturn(branchForComparingHappyFlow.getContactInfo());
        when(branchRepository.save(newBranchHappyFlow)).thenReturn(branchForComparingHappyFlow);

        BranchModel result = branchService.createNewBranch(newBranchHappyFlow);

        assertEquals(branchForComparingHappyFlow.getAdminId(), result.getAdminId());
        assertEquals(branchForComparingHappyFlow.getBranchId(), result.getBranchId());
        assertEquals(branchForComparingHappyFlow.getBranchName(), result.getBranchName());
        assertEquals(branchForComparingHappyFlow.getContactInfo().getId(), result.getContactInfo().getId());
        assertEquals(branchForComparingHappyFlow.getContactInfo().getPhoneNumber(), result.getContactInfo().getPhoneNumber());
    }

    @Test
    public void getBranchExists() {
        when(branchRepository.findById("TT-XXX-0000")).thenReturn(Optional.of(newBranchAlreadyExists));
        Optional<BranchModel> result = branchService.getBranch("TT-XXX-0000");

        assertNotNull(result.get());
        assertTrue(result.isPresent());
        assertEquals(newBranchAlreadyExists.getBranchId(), result.get().getBranchId());
    }

    @Test
    public void getBranchNotExists() {
        when(branchRepository.findById("TT-XXX-0000")).thenReturn(Optional.empty());
        Optional<BranchModel> result = branchService.getBranch("TT-XXX-0000");

        assertFalse(result.isPresent());
    }

    @Test
    public void getBranchesTest() {
        when(branchRepository.findAll()).thenReturn(existingBranches);
        List<BranchModel> result = branchService.getBranches();

        assertNotNull(result);
        assertEquals(existingBranches, result);
    }

    @Test
    public void deleteBranchWhenExistsTest() {
        when(branchRepository.deleteBranch("TT-XXX-0000"))
                .thenReturn(Optional.of(newBranchAlreadyExists));

        Optional<BranchModel> result = branchService.deleteBranch("TT-XXX-0000");

        assertTrue(result.isPresent());
        assertEquals(newBranchAlreadyExists, result.get());
    }

    @Test
    public void deleteBranchWhenNotExistsTest() {
        when(branchRepository.deleteBranch("TT-XXX-0000"))
                .thenReturn(Optional.empty());

        Optional<BranchModel> result = branchService.deleteBranch("TT-XXX-0000");

        assertFalse(result.isPresent());
    }
}
