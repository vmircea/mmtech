package com.membership.hub.controller;

import com.membership.hub.dto.MembershipAddedRequest;
import com.membership.hub.mapper.MembershipMapper;
import com.membership.hub.model.Membership;
import com.membership.hub.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.Validator;

@RestController
@Validated
@RequestMapping("/api")
public class MembershipManagementController {

    private MembershipService membershipService;
    private MembershipMapper membershipMapper;
    @Autowired
    private Validator validator;

    public MembershipManagementController(MembershipMapper membershipMapper, MembershipService membershipService) {
        this.membershipMapper = membershipMapper;
        this.membershipService = membershipService;
    }

    @PostMapping("/members")
    public ResponseEntity<Membership> addNewMembership(
            @Valid
            @RequestBody MembershipAddedRequest memberRequest
            ) {
        Membership newMembership = this.membershipMapper.membershipRequestToMembership(memberRequest);
        System.out.println(newMembership);
        Membership createdMembership = this.membershipService.createNewMembership(newMembership);
        return null;
    }
}
