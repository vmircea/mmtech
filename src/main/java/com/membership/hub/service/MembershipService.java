package com.membership.hub.service;

import com.membership.hub.exception.MembershipExistsException;
import com.membership.hub.model.ContactInfo;
import com.membership.hub.model.Membership;
import com.membership.hub.repository.ContactRepository;
import com.membership.hub.repository.MembershipRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final ContactRepository contactRepository;

    public MembershipService(MembershipRepository membershipRepository, ContactRepository contactRepository) {
        this.membershipRepository = membershipRepository;
        this.contactRepository = contactRepository;
    }

    public Membership createNewMembership(Membership newMembership) {
        if(membershipRepository.findAll().stream()
                .anyMatch(dataBaseItem ->
                dataBaseItem.getName().equals(newMembership.getName()) ||
                dataBaseItem.getContactInfo().getPhoneNumber().equals(newMembership.getContactInfo().getPhoneNumber()))) {
            throw new MembershipExistsException("This name or phoneName already exists in the database");
        }
        ContactInfo addedContactInfo = contactRepository.save(newMembership.getContactInfo());
        newMembership.setId(UUID.randomUUID().toString());
        newMembership.getContactInfo().setId(addedContactInfo.getId());
        return membershipRepository.save(newMembership);
    }

    public Optional<Membership> getMembership(String id) {
        return this.membershipRepository.findById(id);
    }

    public List<Membership> getMemberships() {
        return this.membershipRepository.findAll();
    }

    public boolean updateMembership(Membership membership) {
        Optional<Membership> findMembership = this.membershipRepository.findById(membership.getId());

        if(findMembership.isPresent()) {
            contactRepository.save(membership.getContactInfo());
            membershipRepository.save(membership);
            return true;
        }
        return false;
    }
}
