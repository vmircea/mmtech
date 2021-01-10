package com.membership.hub.service;

import com.membership.hub.exception.MembershipExistsException;
import com.membership.hub.model.ContactInfo;
import com.membership.hub.model.MemberProfession;
import com.membership.hub.model.MemberStatus;
import com.membership.hub.model.Membership;
import com.membership.hub.repository.ContactRepository;
import com.membership.hub.repository.MembershipRepository;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
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
public class MembersipServiceIntegrationTest {

    @Autowired
    private MembershipService service;

    @Autowired
    private MembershipRepository membershipRepository;
    @Autowired
    private ContactRepository contactRepository;

//    @AfterEach
//    public void tearDown() {
//        System.out.println(membershipRepository.findAll());
//        membershipRepository.deleteAll();
//        contactRepository.deleteAll();
//    }

    @Test
//    @RepeatedTest(2)
    public void addMembershipHappyFlow() {
        Membership membership = new Membership("Cristian Popescu", 13, MemberStatus.ACTIVE, MemberProfession.LAWYER,
                new ContactInfo("+40798787958", "@test.com", "Romania", "Bucharest", "Independentei 2", "2 C"));

        Membership createdMembership = service.createNewMembership(membership);

        assertNotNull(createdMembership.getId());
        assertEquals(createdMembership.getName(), membership.getName());
        assertEquals(createdMembership.getStatus(), membership.getStatus());
        assertEquals(createdMembership.getProfession(), membership.getProfession());
        assertEquals(createdMembership.getContactInfo().getId(), membership.getContactInfo().getId());
    }

    @Test
    public void addMembershipNameOrPhoneNumberExists() {
        Membership membership = new Membership("John Doe", 35, MemberStatus.ACTIVE, MemberProfession.DOCTOR,
                new ContactInfo("+40759698745", "john.doe@gmail.com", "United Kingdom", "London", "ParkWay 23", "24"));

        MembershipExistsException exception = assertThrows(MembershipExistsException.class, () -> System.out.println(service.createNewMembership(membership)));

        assertEquals("This name or phoneName already exists in the database", exception.getMessage());
    }
}
