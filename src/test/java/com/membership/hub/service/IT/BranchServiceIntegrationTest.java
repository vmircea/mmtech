package com.membership.hub.service.IT;

import com.membership.hub.exception.BranchException;
import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.model.shared.ContactInfo;
import com.membership.hub.repository.BranchRepository;
import com.membership.hub.service.BranchService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@TestPropertySource(locations = "classpath:application-test.properties")
public class BranchServiceIntegrationTest {

    @Autowired
    private BranchService service;
    @Autowired
    private BranchRepository branchRepository;

    private static BranchModel branchModel;
    private static BranchModel branchModelFromDB;
    private static ContactInfo contactInfoForComparing;

    @BeforeAll
    public static void setup() {
        ContactInfo contactInfo = new ContactInfo(
                "+40700999000",
                "new@test.com",
                "CountryTest",
                "CityTest",
                "StreetTest",
                "BuildingTest"
        );
        branchModel = new BranchModel(
                "TT-XXX-0000",
                "4e48d7a8-64b9-4455-a37f-903fd62def32",
                "Test Name No 1",
                contactInfo,
                0.0);
        branchModelFromDB = new BranchModel(
                "XX-TTT-0000",
                "4e48d7a8-64b9-4455-a37f-903fd62def32",
                "Test Name No 3",
                contactInfo,
                0.0
        );
        contactInfoForComparing = contactInfo;
        contactInfoForComparing.setId(1);
    }

    @Test
    public void createNewBranchHappyFlow() {
            BranchModel result = service.createNewBranch(branchModel);

            assertEquals(branchModel.getBranchId(), result.getBranchId());
            assertEquals(branchModel.getBranchName(), result.getBranchName());
            assertEquals(branchModel.getAdminId(), result.getAdminId());
            assertEquals(contactInfoForComparing.getId(), result.getContactInfo().getId());
            assertEquals(contactInfoForComparing.getPhoneNumber(), result.getContactInfo().getPhoneNumber());
            assertEquals(contactInfoForComparing.getEmailAddress(), result.getContactInfo().getEmailAddress());
            assertEquals(contactInfoForComparing.getCountry(), result.getContactInfo().getCountry());
            assertEquals(contactInfoForComparing.getCity(), result.getContactInfo().getCity());
            assertEquals(contactInfoForComparing.getStreet(), result.getContactInfo().getStreet());
            assertEquals(contactInfoForComparing.getBuilding(), result.getContactInfo().getBuilding());
    }

    @Test
    public void createNewBranchWhenExists() {
        branchRepository.save(branchModel);

        BranchException exception = assertThrows(
                BranchException.class,
                () -> service.createNewBranch(branchModel));

        assertEquals(BranchException.BranchErrors.BRANCH_ALREADY_EXISTS_WITH_ID, exception.getError());
    }

    @Test
    public void getBranchWhenExists() {
        branchRepository.save(branchModel);

        Optional<BranchModel> result = service.getBranch(branchModel.getBranchId());

        assertTrue(result.isPresent());
        assertEquals(branchModel.getBranchId(), result.get().getBranchId());
        assertEquals(branchModel.getBranchName(), result.get().getBranchName());
        assertEquals(branchModel.getAdminId(), result.get().getAdminId());
    }

    @Test
    public void getBranchWhenNotExists() {
        Optional<BranchModel> result = service.getBranch(branchModel.getBranchId());
        assertFalse(result.isPresent());
    }

    @Test
    public void getBranchesTest() {
        branchRepository.save(branchModel);

        List<BranchModel> resultList = service.getBranches();

        assertEquals(branchModelFromDB.getBranchId(), resultList.get(0).getBranchId());
    }

    @Test
    public void deleteBranchWhenNotExists() {
        Optional<BranchModel> result = service.deleteBranch(branchModel.getBranchId());

        assertFalse(result.isPresent());
    }

    @Test
    public void deleteBranchWhenExists() {
        branchRepository.save(branchModel);

        Optional<BranchModel> result = service.deleteBranch(branchModel.getBranchId());

        assertTrue(result.isPresent());
        assertEquals(branchModel.getBranchName(), result.get().getBranchName());
        assertEquals(branchModel.getAdminId(), result.get().getAdminId());
        assertEquals(branchModel.getBranchId(), result.get().getBranchId());
    }

}
