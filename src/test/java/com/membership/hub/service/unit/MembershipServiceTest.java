package com.membership.hub.service.unit;

import com.membership.hub.exception.BranchException;
import com.membership.hub.exception.MembershipException;
import com.membership.hub.mapper.PaymentModelFactory;
import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.model.membership.*;
import com.membership.hub.model.shared.ContactInfo;
import com.membership.hub.model.shared.PaymentModel;
import com.membership.hub.model.shared.Skills;
import com.membership.hub.repository.*;

import com.membership.hub.repository.members.MembershipRepository;
import com.membership.hub.repository.members.SkillsRepository;
import com.membership.hub.repository.shared.ContactRepository;
import com.membership.hub.service.MembershipService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {

    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private ContactRepository contactRepository;
    @Mock
    private SkillsRepository skillsRepository;
    @Mock
    private BranchRepository branchRepository;
    @Mock
    private PaymentsRepository paymentsRepository;
    @Mock
    private PaymentModelFactory paymentModelFactory;

    @InjectMocks
    private MembershipService membershipService;

    private static Membership newMembershipForAddedDuplicateName;
    private static Membership newMembershipForAddedDuplicatePhoneNumber;
    private static Membership newMembershipForAddedNoDuplicate;
    private static Membership membershipForComparing;
    private static Membership updatedMembership;
    private static Membership updatedMembershipNotPresent;

    private static ContactInfo existingContactInfo1;

    private static ContactInfo contactForComparing;

    private static List<Membership> existingMemberships;
    private static List<Membership> comparingMemberships;

    private static ArrayList<Skills> listSkills;
    private static ArrayList<Skills> emptySkillsList;

    private static ArrayList<MembershipFeeModel> listFees;
    private static ArrayList<MembershipFeeModel> emptyFeesList;
    private static BranchModel branchModel;


    @BeforeAll
    public static void setup() {

        // For case: Name already exists
        ContactInfo newContactInfoNoDuplicateNumber = new ContactInfo("+40798787958", "@test.com", "Romania", "Bucharest", "Independentei 2", "2 C");
        newMembershipForAddedDuplicateName = new Membership("Matei Costin", "UK-LDN-5645", 36, MemberStatus.ACTIVE, MemberProfession.IT, newContactInfoNoDuplicateNumber);

        // For case:  Phone number already exists
        ContactInfo newContactInfoDuplicateNumber = new ContactInfo("+40748798999", "test@test.com", "Romania", "Bucharest", "Aviatiei 29", "63 A");
        newMembershipForAddedDuplicatePhoneNumber = new Membership("Matei Costin", "UK-LDN-5645", 50 ,MemberStatus.ACTIVE, MemberProfession.DOCTOR, newContactInfoDuplicateNumber);

        //For case: Neither name or phone number exist in data base.
        // Membership entity to send on Service method:
        newMembershipForAddedNoDuplicate = new Membership("Cristian Popescu", "UK-LDN-5645", 13, MemberStatus.ACTIVE, MemberProfession.LAWYER, newContactInfoNoDuplicateNumber);
        // Contact and Membership entities to compare in the end:
        contactForComparing = new ContactInfo(1,"+40798787958", "@test.com", "Romania", "Bucharest", "Independentei 2", "2 C");
        membershipForComparing = new Membership("a2442fdc-2fg1-4f01-bbc5-bf434572v441g", "Cristian Popescu", "UK-LDN-5645", 13, MemberStatus.ACTIVE, MemberProfession.LAWYER, contactForComparing);
        // MemberList to compare

        // Like a DatBase
        existingContactInfo1 = new ContactInfo(1, "+40784987589", "test1@test.com", "Romania", "Bucharest", "Aviatiei 13", "63 A");
        ContactInfo existingContactInfo2 = new ContactInfo(2, "+40748798999", "test2@test.com", "Romania", "Bucharest", "Aviatiei 13", "63 A");
        ContactInfo existingContactInfo3 = new ContactInfo(3, "+40796358748", "test3@test.com", "Romania", "Bucharest", "Mosilor 10", "23 A");
        existingMemberships = new ArrayList<>();
        existingMemberships.add(new Membership("b9462fdc-2f01-4f02-bbc3-ba433572a411", "Matei Costin", "UK-LDN-5645", 36, MemberStatus.INACTIVE, MemberProfession.DOCTOR, existingContactInfo1));
        existingMemberships.add(new Membership("1a4f5a32-1c5d-4f90-a4cf-266d5f91f533",  "Matei Apetrei", "UK-LDN-5645", 50 ,MemberStatus.ACTIVE, MemberProfession.DOCTOR, existingContactInfo2));
        existingMemberships.add(new Membership("4baae4d7-bbb9-4a4b-a298-edd846fb7440",  "Alex Ciobanu", "UK-LDN-5645",  40, MemberStatus.ACTIVE, MemberProfession.IT, existingContactInfo3));

        comparingMemberships = new ArrayList<>();
        comparingMemberships.add(new Membership("b9462fdc-2f01-4f02-bbc3-ba433572a411", "Matei Costin", "UK-LDN-5645", 36, MemberStatus.INACTIVE, MemberProfession.DOCTOR, existingContactInfo1));
        comparingMemberships.add(new Membership("1a4f5a32-1c5d-4f90-a4cf-266d5f91f533",  "Matei Apetrei", "UK-LDN-5645", 50 ,MemberStatus.ACTIVE, MemberProfession.DOCTOR, existingContactInfo2));
        comparingMemberships.add(new Membership("4baae4d7-bbb9-4a4b-a298-edd846fb7440",  "Alex Ciobanu", "UK-LDN-5645", 40, MemberStatus.ACTIVE, MemberProfession.IT, existingContactInfo3));


        // Membership and Contact for Update Happy Flow
        ContactInfo updatedContact = new ContactInfo(1, "+40755888111", "test1@test.com", "Romania", "Bucharest", "Aviatiei 13", "63 A");
        updatedMembership = new Membership("b9462fdc-2f01-4f02-bbc3-ba433572a411",  "Matei Costin", "UK-LDN-5645",37, MemberStatus.INACTIVE, MemberProfession.DOCTOR, updatedContact);

        // Membership and Contact for Update which is no longer in the database
        updatedMembershipNotPresent = new Membership("b9462fdW-2f01-4f02-bbc3-ba433572a411",  "Matei Costin", "UK-LDN-5645", 37, MemberStatus.INACTIVE, MemberProfession.DOCTOR, updatedContact);

        // Skills
        listSkills = new ArrayList<>();
        listSkills.add(Skills.CODING);
        listSkills.add(Skills.MARKETING);
        emptySkillsList = new ArrayList<>();

        // Fees
        listFees = new ArrayList<>();
        LocalDate date = LocalDate.parse("2020-02-12");
        listFees.add(new MembershipFeeModel(date, 35.6));
        emptyFeesList = new ArrayList<>();

        // Branch
        branchModel = new BranchModel(membershipForComparing.getBranchId(),
                membershipForComparing.getId(), "Camden East Town", contactForComparing);
    }

    @Test
    public void addMembershipBadFlowExistingName() {
        when(membershipRepository.findAll()).thenReturn(existingMemberships);

        MembershipException exception = assertThrows(MembershipException.class, () -> membershipService.createNewMembership(newMembershipForAddedDuplicateName));

        assertEquals(MembershipException.MembershipErrors.MEMBERSHIP_ALREADY_EXISTS, exception.getError());
    }

    @Test
    public void addMembershipBadFlowExistingPhoneNumber() {
        when(membershipRepository.findAll()).thenReturn(existingMemberships);
        MembershipException exception = assertThrows(MembershipException.class, () -> membershipService.createNewMembership(newMembershipForAddedDuplicatePhoneNumber));
        assertEquals(MembershipException.MembershipErrors.MEMBERSHIP_ALREADY_EXISTS, exception.getError());
    }

    @Test
    public void addMembershipHappyFlow() {
        when(membershipRepository.findAll()).thenReturn(existingMemberships);
        when(contactRepository.save(newMembershipForAddedNoDuplicate.getContactInfo())).thenReturn(contactForComparing);
        when(membershipRepository.save(newMembershipForAddedNoDuplicate)).thenReturn(membershipForComparing);

        Membership result = membershipService.createNewMembership(newMembershipForAddedNoDuplicate);

        assertNotNull(result.getId());
        assertEquals(membershipForComparing.getName(), result.getName());
        assertEquals(membershipForComparing.getAge(), result.getAge());
        assertEquals(membershipForComparing.getStatus(), result.getStatus());
        assertEquals(membershipForComparing.getProfession(), result.getProfession());

        assertEquals(membershipForComparing.getContactInfo().getId(), result.getContactInfo().getId());
        assertEquals(membershipForComparing.getContactInfo().getPhoneNumber(), result.getContactInfo().getPhoneNumber());
        assertEquals(membershipForComparing.getContactInfo().getEmailAddress(), result.getContactInfo().getEmailAddress());
        assertEquals(membershipForComparing.getContactInfo().getCountry(), result.getContactInfo().getCountry());
        assertEquals(membershipForComparing.getContactInfo().getCity(), result.getContactInfo().getCity());
        assertEquals(membershipForComparing.getContactInfo().getBuilding(), result.getContactInfo().getBuilding());
    }

    @Test
    public void updateMembershipHappyFlow() {
        membershipService.updateMembership(updatedMembership);

        verify(contactRepository, times(1)).save(updatedMembership.getContactInfo());
        verify(membershipRepository, times(1)).save(updatedMembership);
    }

    @Test
    public void getMembershipTestHappyFlow() {
        Membership existingMembership = existingMemberships.get(0);
        when(membershipRepository.findById(existingMembership.getId())).thenReturn(Optional.of(existingMembership));
        when(skillsRepository.findById(existingMembership.getId())).thenReturn(listSkills);
        when(paymentsRepository.findAllMembershipFeesById(existingMembership.getId())).thenReturn(listFees);
        Optional<Membership> result = membershipService.getMembership(existingMembership.getId());
        assertTrue(result.isPresent());
        assertNotNull(result.get().getSkills());
        assertNotNull(result.get().getPaidInFeeDetails());
    }

    @Test
    public void getMembershipTestNotExist() {
        Membership existingMembership = existingMemberships.get(0);
        when(membershipRepository.findById(existingMembership.getId())).thenReturn(Optional.empty());
        when(skillsRepository.findById(existingMembership.getId())).thenReturn(emptySkillsList);
        when(paymentsRepository.findAllMembershipFeesById(existingMembership.getId())).thenReturn(emptyFeesList);
        Optional<Membership> result = membershipService.getMembership(existingMembership.getId());
        assertFalse(result.isPresent());
    }

    @Test
    public void getMembershipsByBranchId() {
        comparingMemberships.get(0).setSkills(listSkills);
        comparingMemberships.get(0).setPaidInFeeDetails(listFees);
        comparingMemberships.get(1).setSkills(emptySkillsList);
        comparingMemberships.get(1).setPaidInFeeDetails(emptyFeesList);
        comparingMemberships.get(2).setSkills(emptySkillsList);
        comparingMemberships.get(2).setPaidInFeeDetails(emptyFeesList);

        when(membershipRepository.findAllByBranch(branchModel.getBranchId())).thenReturn(existingMemberships);
        when(skillsRepository.findById(existingMemberships.get(0).getId())).thenReturn(listSkills);
        when(paymentsRepository.findAllMembershipFeesById(existingMemberships.get(0).getId())).thenReturn(listFees);
        when(skillsRepository.findById(existingMemberships.get(1).getId())).thenReturn(emptySkillsList);
        when(paymentsRepository.findAllMembershipFeesById(existingMemberships.get(1).getId())).thenReturn(emptyFeesList);
        when(skillsRepository.findById(existingMemberships.get(2).getId())).thenReturn(emptySkillsList);
        when(paymentsRepository.findAllMembershipFeesById(existingMemberships.get(2).getId())).thenReturn(emptyFeesList);

        List<Membership> result = membershipService.getMembershipsByBranchId(branchModel.getBranchId());

        assertEquals(comparingMemberships.get(0).getBranchId(), result.get(0).getBranchId());
        assertEquals(comparingMemberships.get(0).getPaidInFeeDetails(), result.get(0).getPaidInFeeDetails());
        assertEquals(comparingMemberships.get(0).getSkills(), result.get(0).getSkills());
        assertEquals(comparingMemberships.get(1).getBranchId(), result.get(1).getBranchId());
        assertEquals(comparingMemberships.get(1).getPaidInFeeDetails(), result.get(1).getPaidInFeeDetails());
        assertEquals(comparingMemberships.get(1).getSkills(), result.get(1).getSkills());
        assertEquals(comparingMemberships.get(2).getSkills(), result.get(2).getSkills());
        assertEquals(comparingMemberships.get(2).getSkills(), result.get(2).getSkills());
        assertEquals(comparingMemberships.get(2).getSkills(), result.get(2).getSkills());
    }

    @Test
    public void getAllMembershipsTest() {
        comparingMemberships.get(0).setSkills(listSkills);
        comparingMemberships.get(0).setPaidInFeeDetails(listFees);

        when(membershipRepository.findAll()).thenReturn(existingMemberships);
        when(skillsRepository.findById(existingMemberships.get(0).getId())).thenReturn(listSkills);
        when(paymentsRepository.findAllMembershipFeesById(existingMemberships.get(0).getId())).thenReturn(listFees);
        when(skillsRepository.findById(existingMemberships.get(1).getId())).thenReturn(emptySkillsList);
        when(paymentsRepository.findAllMembershipFeesById(existingMemberships.get(1).getId())).thenReturn(emptyFeesList);
        when(skillsRepository.findById(existingMemberships.get(2).getId())).thenReturn(emptySkillsList);
        when(paymentsRepository.findAllMembershipFeesById(existingMemberships.get(2).getId())).thenReturn(emptyFeesList);
        List<Membership> result = membershipService.getMemberships();
        assertEquals(comparingMemberships.get(0).getName(), result.get(0).getName());
        assertEquals(comparingMemberships.get(0).getSkills(), result.get(0).getSkills());
        assertEquals(comparingMemberships.get(0).getPaidInFeeDetails(), result.get(0).getPaidInFeeDetails());
    }

    @Test
    public void addSkillsToMemberTest() {
        membershipService.addSkillsToMember(listSkills, membershipForComparing.getId());
        verify(skillsRepository, times(1)).save(listSkills.get(0), membershipForComparing.getId());
        verify(skillsRepository, times(1)).save(listSkills.get(1), membershipForComparing.getId());
    }

    @Test
    public void getSkillsByIdTest() {
        String id = "b9462fdc-2f01-4f02-bbc3-ba433572a411";
        when(skillsRepository.findById(id)).thenReturn(listSkills);

        List<Skills> result = membershipService.getSkillsById(id);

        assertEquals(listSkills.get(0), result.get(0));
        assertEquals(listSkills.get(1), result.get(1));
    }

    @Test
    public void getSkillsByIdBadFlowTest() {
        String id = "b9462fdc-2f01-4f02-bbc3-ba433572a411";
        when(skillsRepository.findById(id)).thenReturn(emptySkillsList);

        List<Skills> result = membershipService.getSkillsById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    public void addFeeToMemberTest() {
        LocalDate date = LocalDate.parse("2020-02-12");
        MembershipFeeModel fee = new MembershipFeeModel(date, 35.6);
        String description = String.format("Paid monthly membership by %s into branch %s", membershipForComparing.getName(), branchModel.getBranchName());
        PaymentModel transaction = new PaymentModel(
                membershipForComparing.getId(),
                branchModel.getBranchId(),
                fee.getPaidInAmount(),
                fee.getPaidInDate(),
                description);

        when(branchRepository.findById(membershipForComparing.getBranchId())).thenReturn(Optional.of(branchModel));
        when(paymentModelFactory.createPaymentModel(
                membershipForComparing.getId(),
                branchModel.getBranchId(),
                fee.getPaidInAmount(),
                fee.getPaidInDate(),
                description)).thenReturn(transaction);

        membershipService.addFeeToMember(fee, membershipForComparing);

        verify(paymentsRepository, times(1)).save(transaction);
        verify(branchRepository, times(1)).updateAmount(membershipForComparing.getBranchId(), transaction.getAmount());
    }

    @Test
    public void addFeeToMemberBranchNotExists() {
        LocalDate date = LocalDate.parse("2020-02-12");
        MembershipFeeModel fee = new MembershipFeeModel(date, 35.6);
        String description = String.format("Paid monthly membership by %s into branch %s", membershipForComparing.getName(), branchModel.getBranchName());
        PaymentModel transaction = new PaymentModel(
                membershipForComparing.getId(),
                branchModel.getBranchId(),
                35.6,
                date,
                description);

        when(branchRepository.findById(any())).thenReturn(Optional.empty());

        BranchException exception = assertThrows(BranchException.class, () -> membershipService.addFeeToMember(fee, membershipForComparing));

        assertEquals(BranchException.BranchErrors.BRANCH_NOT_FOUND, exception.getError());

        verify(paymentsRepository, times(0)).save(transaction);
        verify(branchRepository, times(0)).updateAmount(membershipForComparing.getBranchId(), transaction.getAmount());
    }

    @Test
    public void getFeesByIdTest() {
        String id = "b9462fdc-2f01-4f02-bbc3-ba433572a411";
        when(paymentsRepository.findAllMembershipFeesById(id)).thenReturn(listFees);

        List<MembershipFeeModel> result = membershipService.getFeesById(id);

        assertEquals(listFees.get(0), result.get(0));
    }

    @Test
    public void getFeesByIdBadFlowTest() {
        String id = "b9462fdc-2f01-4f02-bbc3-ba433572a411";
        when(paymentsRepository.findAllMembershipFeesById(id)).thenReturn(emptyFeesList);

        List<MembershipFeeModel> result = membershipService.getFeesById(id);

        assertTrue(result.isEmpty());
    }
}
