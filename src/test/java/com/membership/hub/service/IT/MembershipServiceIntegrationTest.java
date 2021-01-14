package com.membership.hub.service.IT;

import com.membership.hub.exception.MembershipException;
import com.membership.hub.model.shared.ContactInfo;
import com.membership.hub.model.membership.MemberProfession;
import com.membership.hub.model.membership.MemberStatus;
import com.membership.hub.model.membership.Membership;
import com.membership.hub.service.MembershipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@TestPropertySource(locations = "classpath:application-test.properties")
public class MembershipServiceIntegrationTest {

    @Autowired
    private MembershipService service;

//    @Autowired
//    private MembershipRepository membershipRepository;
//    @Autowired
//    private ContactRepository contactRepository;

//    @AfterEach
//    public void tearDown() {
//        System.out.println(membershipRepository.findAll());
//        membershipRepository.deleteAll();
//        contactRepository.deleteAll();
//    }

    @Test
//    @RepeatedTest(2)
    public void addMembershipHappyFlow() {
        Membership membership = new Membership("Cristian Popescu","UK-LDN-5645", 13, MemberStatus.ACTIVE, MemberProfession.LAWYER,
                new ContactInfo("+40798787958", "@test.com", "Romania", "Bucharest", "Independentei 2", "2 C"));
        Membership membershipExpected = new Membership("Cristian Popescu","UK-LDN-5645", 13, MemberStatus.ACTIVE, MemberProfession.LAWYER,
                new ContactInfo("+40798787958", "@test.com", "Romania", "Bucharest", "Independentei 2", "2 C"));
        Membership createdMembership = service.createNewMembership(membership);

        assertNotNull(createdMembership.getId());
        assertEquals(membershipExpected.getName(), createdMembership.getName());
        assertEquals(membershipExpected.getStatus(), createdMembership.getStatus());
        assertEquals(membershipExpected.getProfession(), createdMembership.getProfession());
        assertEquals(membershipExpected.getContactInfo().getId(), createdMembership.getContactInfo().getId());
    }

    @Test
    public void addMembershipNameOrPhoneNumberExists() {
        Membership membership = new Membership("John Doe","UK-LDN-5645", 35, MemberStatus.ACTIVE, MemberProfession.DOCTOR,
                new ContactInfo("+40759698745", "john.doe@gmail.com", "United Kingdom", "London", "ParkWay 23", "24"));

        MembershipException exception = assertThrows(MembershipException.class, () -> service.createNewMembership(membership));

        assertEquals(MembershipException.MembershipErrors.MEMBERSHIP_ALREADY_EXISTS, exception.getError());
    }
}
