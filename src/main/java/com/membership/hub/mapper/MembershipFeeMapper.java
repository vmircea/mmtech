package com.membership.hub.mapper;

import com.membership.hub.dto.MembershipFeeRequest;
import com.membership.hub.model.MembershipFeeModel;
import org.springframework.stereotype.Component;

@Component
public class MembershipFeeMapper {
    public MembershipFeeModel membershipFeeRequestToMembershipFeeModel(Double paidInAmount) {
        MembershipFeeModel membershipFeeModel = new MembershipFeeModel(paidInAmount);
        return membershipFeeModel;
    }
}
