package com.membership.hub.mapper;

import com.membership.hub.dto.MembershipAddedRequest;
import com.membership.hub.model.ContactInfo;
import com.membership.hub.model.Membership;
import org.springframework.stereotype.Component;

@Component
public class MembershipMapper {

    public Membership membershipRequestToMembership(MembershipAddedRequest membershipAddedRequest) {
        return new Membership(
                membershipAddedRequest.getName(),
                membershipAddedRequest.getAge(),
                membershipAddedRequest.getStatus(),
                membershipAddedRequest.getProfession(),
                membershipAddedRequest.getContactInfo());
    }
}
