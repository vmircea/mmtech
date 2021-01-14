package com.membership.hub.mapper;

import com.membership.hub.model.membership.MembershipFeeModel;
import org.springframework.stereotype.Component;

@Component
public class MembershipFeeMapper {
    public MembershipFeeModel membershipFeeRequestToMembershipFeeModel(Double paidInAmount) {
        return new MembershipFeeModel(paidInAmount);
    }
}
