package com.membership.hub.service;

import com.membership.hub.exception.MembershipExistsException;
import com.membership.hub.model.ContactInfo;
import com.membership.hub.model.MemberProfession;
import com.membership.hub.model.MemberStatus;
import com.membership.hub.model.Membership;
import com.membership.hub.repository.ContactRepository;
import com.membership.hub.repository.MembershipRepository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

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

    @InjectMocks
    private MembershipService membershipService;

    private static Membership newMembershipForAddedDuplicateName;
    private static Membership newMembershipForAddedDuplicatePhoneNumber;
    private static Membership newMembershipForAddedNoDuplicate;
    private static Membership membershipForComparing;
    private static Membership updatedMembership;
    private static Membership updatedMembershipNotPresent;

    private static ContactInfo contactForComparing;

    private static List<Membership> existingMemberships;


    @BeforeAll
    public static void setup() {

        // For case: Name already exists
        ContactInfo newContactInfoNoDuplicateNumber = new ContactInfo("+40798787958", "@test.com", "Romania", "Bucharest", "Independentei 2", "2 C");
        newMembershipForAddedDuplicateName = new Membership("Matei Costin", 36, MemberStatus.ACTIVE, MemberProfession.IT, newContactInfoNoDuplicateNumber);

        // For case:  Phone number already exists
        ContactInfo newContactInfoDuplicateNumber = new ContactInfo("+40748798999", "test@test.com", "Romania", "Bucharest", "Aviatiei 29", "63 A");
        newMembershipForAddedDuplicatePhoneNumber = new Membership("Matei Costin", 50 ,MemberStatus.ACTIVE, MemberProfession.DOCTOR, newContactInfoDuplicateNumber);

        //For case: Neither name or phone number exist in data base.
        // Membership entity to send on Service method:
        newMembershipForAddedNoDuplicate = new Membership("Cristian Popescu", 13, MemberStatus.ACTIVE, MemberProfession.LAWYER, newContactInfoNoDuplicateNumber);
        // Contact and Membership entities to compare in the end:
        contactForComparing = new ContactInfo(1,"+40798787958", "@test.com", "Romania", "Bucharest", "Independentei 2", "2 C");
        membershipForComparing = new Membership("a2442fdc-2fg1-4f01-bbc5-bf434572v441g","Cristian Popescu", 13, MemberStatus.ACTIVE, MemberProfession.LAWYER, contactForComparing);

        // Like a DatBase
        ContactInfo existingContactInfo1 = new ContactInfo(1, "+40784987589", "test1@test.com", "Romania", "Bucharest", "Aviatiei 13", "63 A");
        ContactInfo existingContactInfo2 = new ContactInfo(2, "+40748798999", "test2@test.com", "Romania", "Bucharest", "Aviatiei 13", "63 A");
        ContactInfo existingContactInfo3 = new ContactInfo(3, "+40796358748", "test3@test.com", "Romania", "Bucharest", "Mosilor 10", "23 A");
        existingMemberships = new ArrayList<>();
        existingMemberships.add(new Membership("b9462fdc-2f01-4f02-bbc3-ba433572a411", "Matei Costin", 36, MemberStatus.INACTIVE, MemberProfession.DOCTOR, existingContactInfo1));
        existingMemberships.add(new Membership("1a4f5a32-1c5d-4f90-a4cf-266d5f91f533","Matei Apetrei", 50 ,MemberStatus.ACTIVE, MemberProfession.DOCTOR, existingContactInfo2));
        existingMemberships.add(new Membership("4baae4d7-bbb9-4a4b-a298-edd846fb7440","Alex Ciobanu", 40, MemberStatus.ACTIVE, MemberProfession.IT, existingContactInfo3));

        // Membership and Contact for Update Happy Flow
        ContactInfo updatedContact = new ContactInfo(1, "+40755888111", "test1@test.com", "Romania", "Bucharest", "Aviatiei 13", "63 A");
        updatedMembership = new Membership("b9462fdc-2f01-4f02-bbc3-ba433572a411", "Matei Costin", 37, MemberStatus.INACTIVE, MemberProfession.DOCTOR, updatedContact);

        // Membership and Contact for Update which is no longer in the database
        updatedMembershipNotPresent = new Membership("b9462fdW-2f01-4f02-bbc3-ba433572a411", "Matei Costin", 37, MemberStatus.INACTIVE, MemberProfession.DOCTOR, updatedContact);
    }

    @Test
    public void addMembershipBadFlowExistingName() {
        when(membershipRepository.findAll()).thenReturn(existingMemberships);

        MembershipExistsException exception = assertThrows(MembershipExistsException.class, () -> membershipService.createNewMembership(newMembershipForAddedDuplicateName));

        assertEquals("This name or phoneName already exists in the database", exception.getMessage());
    }

    @Test
    public void addMembershipBadFlowExistingPhoneNumber() {
        when(membershipRepository.findAll()).thenReturn(existingMemberships);
        MembershipExistsException exception = assertThrows(MembershipExistsException.class, () -> membershipService.createNewMembership(newMembershipForAddedDuplicatePhoneNumber));
        assertEquals("This name or phoneName already exists in the database", exception.getMessage());
    }

    @Test
    public void addMembershipHappyFlow() {
        when(membershipRepository.findAll()).thenReturn(existingMemberships);
        when(contactRepository.save(newMembershipForAddedNoDuplicate.getContactInfo())).thenReturn(contactForComparing);
        when(membershipRepository.save(newMembershipForAddedNoDuplicate)).thenReturn(membershipForComparing);

        Membership result = membershipService.createNewMembership(newMembershipForAddedNoDuplicate);

        assertNotNull(result.getId());
        assertEquals(membershipForComparing.getName(), result.getName());
        assertEquals(result.getAge(), membershipForComparing.getAge());
        assertEquals(result.getStatus(), membershipForComparing.getStatus());
        assertEquals(result.getProfession(), membershipForComparing.getProfession());

        assertEquals(result.getContactInfo().getId(), membershipForComparing.getContactInfo().getId());
        assertEquals(result.getContactInfo().getPhoneNumber(), membershipForComparing.getContactInfo().getPhoneNumber());
        assertEquals(result.getContactInfo().getEmailAddress(), membershipForComparing.getContactInfo().getEmailAddress());
        assertEquals(result.getContactInfo().getCountry(), membershipForComparing.getContactInfo().getCountry());
        assertEquals(result.getContactInfo().getCity(), membershipForComparing.getContactInfo().getCity());
        assertEquals(result.getContactInfo().getStreet(), membershipForComparing.getContactInfo().getStreet());
        assertEquals(result.getContactInfo().getBuilding(), membershipForComparing.getContactInfo().getBuilding());
    }

    @Test
    public void updateMembershipHappyFlow() {
        Optional<Membership> optional = Optional.of(existingMemberships.get(0));
        when(membershipRepository.findById(updatedMembership.getId())).thenReturn(optional);

        Boolean hasBeenUpdated = membershipService.updateMembership(updatedMembership);

        assertEquals(hasBeenUpdated, true);

        verify(contactRepository, times(1)).save(updatedMembership.getContactInfo());
        verify(membershipRepository, times(1)).save(updatedMembership);
    }

    @Test
    public void updateMembershipWhichDoesNotExist() {
        when(membershipRepository.findById(updatedMembershipNotPresent.getId())).thenReturn(Optional.empty());

        Boolean hasBeenUpdated = membershipService.updateMembership(updatedMembershipNotPresent);

        assertEquals(hasBeenUpdated, false);

        verify(contactRepository, times(0)).save(updatedMembershipNotPresent.getContactInfo());
        verify(membershipRepository, times(0)).save(updatedMembershipNotPresent);
    }

    @Test
    public void getMembershipTestHappyFlow() {
        Membership existingMembership = existingMemberships.get(0);
        when(membershipRepository.findById(existingMembership.getId())).thenReturn(Optional.of(existingMembership));
        Optional<Membership> result = membershipService.getMembership(existingMembership.getId());
        assertTrue(result.isPresent());
    }

    @Test
    public void getMembershipTestNotExist() {
        Membership existingMembership = existingMemberships.get(0);
        when(membershipRepository.findById(existingMembership.getId())).thenReturn(Optional.empty());
        Optional<Membership> result = membershipService.getMembership(existingMembership.getId());
        assertFalse(result.isPresent());
    }

    @Test
    public void getAllMembershipsTest() {
        when(membershipRepository.findAll()).thenReturn(existingMemberships);
        List<Membership> result = membershipService.getMemberships();
        assertEquals(result, existingMemberships);
    }
}
