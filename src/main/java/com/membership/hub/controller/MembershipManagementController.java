package com.membership.hub.controller;

import com.membership.hub.dto.MembershipAddedRequest;
import com.membership.hub.dto.MembershipFeeRequest;
import com.membership.hub.mapper.MembershipFeeMapper;
import com.membership.hub.mapper.MembershipMapper;
import com.membership.hub.model.MemberSkill;
import com.membership.hub.model.Membership;
import com.membership.hub.model.MembershipFeeModel;
import com.membership.hub.service.MembershipService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api")
public class MembershipManagementController {

    private final MembershipService membershipService;
    private final MembershipMapper membershipMapper;
    private final MembershipFeeMapper membershipFeeMapper;

    public MembershipManagementController(MembershipMapper membershipMapper, MembershipService membershipService, MembershipFeeMapper membershipFeeMapper) {
        this.membershipMapper = membershipMapper;
        this.membershipService = membershipService;
        this.membershipFeeMapper = membershipFeeMapper;
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<Membership> getMembership(
            @PathVariable String id
    ) {
        Optional<Membership> membership = membershipService.getMembership(id);
        return membership.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/members")
    public ResponseEntity<List<Membership>> getMemberships() {
        List<Membership> memberships = membershipService.getMemberships();

        if (memberships.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.ok(memberships);
        }
    }

    @PostMapping("/members")
    public ResponseEntity<Membership> addNewMembership(
            @Valid
            @RequestBody MembershipAddedRequest memberRequest
    ) {
        Membership newMembership = this.membershipMapper.membershipRequestToMembership(memberRequest);
        Membership createdMembership = this.membershipService.createNewMembership(newMembership);

        return ResponseEntity
                .created(URI.create("/membership/" + createdMembership.getId()))
                .body(createdMembership);
    }

    @PutMapping("/members/{id}")
    public ResponseEntity<Void> updateMembership(
            @PathVariable String id,
            @Valid @RequestBody MembershipAddedRequest memberRequest
    ) {
        Optional<Membership> existingMembership = membershipService.getMembership(id);
        if(existingMembership.isPresent()) {
            Membership newMembership = this.membershipMapper.membershipRequestToMembership(memberRequest);
            existingMembership.get().update(newMembership);
            membershipService.updateMembership(existingMembership.get());
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/members/{id}")
    public ResponseEntity<Void> patchMembership(
            @PathVariable String id,
            @Valid @RequestBody MembershipAddedRequest memberRequest
    ) {
        Optional<Membership> existingMembership = membershipService.getMembership(id);
        if (existingMembership.isPresent()) {
            Membership newMembership = this.membershipMapper.membershipRequestToMembership(memberRequest);
            existingMembership.get().patch(newMembership);
            membershipService.updateMembership(existingMembership.get());
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/skills/{id}")
    public ResponseEntity<Void> addSkills(
            @PathVariable String id,
            @RequestBody List<MemberSkill> skillsRequest
    ) {
        Optional<Membership> existingMembership = membershipService.getMembership(id);
        if(existingMembership.isPresent()) {
            membershipService.addSkillsToMember(skillsRequest, id);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/skills/{id}")
    public ResponseEntity<List<MemberSkill>> getSkillsOfMember(@PathVariable String id) {
        List<MemberSkill> list = membershipService.getSkillsById(id);
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.ok(list);
        }
    }

    @PostMapping("/fees/{id}")
    public ResponseEntity<Void> addMembershipFee(
            @PathVariable String id,
            @RequestBody Double paidInAmount
            ) {
        MembershipFeeModel newFeeAdded = this.membershipFeeMapper.membershipFeeRequestToMembershipFeeModel(paidInAmount);
        Optional<Membership> existingMembership = membershipService.getMembership(id);
        if(existingMembership.isPresent()) {
            List<MembershipFeeModel> newFeesAdded = new ArrayList<>();
            newFeesAdded.add(newFeeAdded);
            membershipService.addFeeToMember(newFeesAdded, id);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("fees/{id}")
    public ResponseEntity<List<MembershipFeeModel>> getFeesOfMember(@PathVariable String id) {
        List<MembershipFeeModel> list = membershipService.getFeesById(id);
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.ok(list);
        }
    }

}
