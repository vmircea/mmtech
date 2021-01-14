package com.membership.hub.mapper;

import com.membership.hub.dto.MembershipAddedRequest;
import com.membership.hub.dto.MembershipUpdatedRequest;
import com.membership.hub.model.membership.Membership;
import org.springframework.stereotype.Component;

@Component
public class MembershipMapper {

    public Membership membershipRequestToMembership(MembershipAddedRequest membershipAddedRequest) {
        return new Membership(
                membershipAddedRequest.getName(),
                membershipAddedRequest.getBranchId(),
                membershipAddedRequest.getAge(),
                membershipAddedRequest.getStatus(),
                membershipAddedRequest.getProfession(),
                membershipAddedRequest.getContactInfo());
    }

    public Membership membershipUpdatedRequestToMembership(MembershipUpdatedRequest membershipUpdatedRequest) {
        return new Membership(
                membershipUpdatedRequest.getId(),
                membershipUpdatedRequest.getName(),
                membershipUpdatedRequest.getBranchId(),
                membershipUpdatedRequest.getAge(),
                membershipUpdatedRequest.getStatus(),
                membershipUpdatedRequest.getProfession(),
                membershipUpdatedRequest.getContactInfo());
    }
}
