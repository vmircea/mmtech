package com.membership.hub.service;

import com.membership.hub.model.Membership;
import com.membership.hub.repository.MembershipRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {
    private MembershipRepository membershipRepository;

    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public Membership createNewMembership(Membership newMembership) {
        return this.membershipRepository.saveMembership(newMembership);
    }
}
