package com.membership.hub.mapper;

import com.membership.hub.dto.BranchRequest;
import com.membership.hub.model.branch.BranchModel;
import org.springframework.stereotype.Component;

@Component
public class BranchModelMapper {

    public BranchModel branchRequestToBranchModel(BranchRequest branchRequest) {
        return new BranchModel(
                branchRequest.getBranchId(),
                branchRequest.getAdminId(),
                branchRequest.getBranchName(),
                branchRequest.getContactInfo()
        );
    }
}
