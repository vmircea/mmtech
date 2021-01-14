package com.membership.hub.controller;

import com.membership.hub.dto.MembershipAddedRequest;
import com.membership.hub.dto.MembershipUpdatedRequest;
import com.membership.hub.exception.MembershipException;
import com.membership.hub.mapper.MembershipFeeMapper;
import com.membership.hub.mapper.MembershipMapper;
import com.membership.hub.model.shared.Skills;
import com.membership.hub.model.membership.Membership;
import com.membership.hub.model.membership.MembershipFeeModel;
import com.membership.hub.service.MembershipService;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/memberships")
@Api(value = "/memberships",
        tags = "Memberships")
public class MembershipManagementController {

    private final MembershipService membershipService;
    private final MembershipMapper membershipMapper;
    private final MembershipFeeMapper membershipFeeMapper;

    public MembershipManagementController(MembershipMapper membershipMapper, MembershipService membershipService, MembershipFeeMapper membershipFeeMapper) {
        this.membershipMapper = membershipMapper;
        this.membershipService = membershipService;
        this.membershipFeeMapper = membershipFeeMapper;
    }

    /**
     *  Membership Basics:
     */

    @ApiOperation(value = "Get Membership by ID",
            notes = "Fetch a membership from DB based on the path ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Membership> getMembership(
            @PathVariable String id
    ) {
        Optional<Membership> membership = membershipService.getMembership(id);
        if(membership.isPresent()) {
            return ResponseEntity.ok(membership.get());
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation(value = "Get all Memberships",
            notes = "Fetch all memberships from DB.")
    @GetMapping
    public ResponseEntity<List<Membership>> getMemberships() {
        List<Membership> memberships = membershipService.getMemberships();

        if (memberships.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.ok(memberships);
        }
    }

    @ApiOperation(value = "Get all Memberships By Branch Id",
            notes = "Fetch all memberships which belong to a certain branch based on the branch ID.")
    @GetMapping("/byBranch/{id}")
    public ResponseEntity<List<Membership>> getMembershipsByBranchId(@PathVariable String id) {
        List<Membership> memberships = membershipService.getMembershipsByBranchId(id);

        if (memberships.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.ok(memberships);
        }
    }

    @ApiOperation(value = "Add a new Membership",
            notes = "Add a new Membership based on the information received in the request")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The Membership was successfully added based on the received request"),
            @ApiResponse(code = 500, message = "Membership cannot be added because already exists"),
            @ApiResponse(code = 400, message = "Validation error on the received request")
    })
    @PostMapping
    public ResponseEntity<Membership> addNewMembership(
            @Valid
            @ApiParam(name = "membership", value = "Membership Basic details", required = true)
            @RequestBody MembershipAddedRequest memberRequest
    ) {
        Membership newMembership = this.membershipMapper.membershipRequestToMembership(memberRequest);

        Membership createdMembership = this.membershipService.createNewMembership(newMembership);
        return ResponseEntity
                .created(URI.create("/membership/" + createdMembership.getId()))
                .body(createdMembership);
    }

    @ApiOperation(value = "Update a Membership",
            notes = "Update the basic details of a membership based on the member ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMembership(
            @PathVariable String id,
            @Valid @RequestBody MembershipUpdatedRequest memberRequest
    ) {
        if (!memberRequest.getId().equals(id)) {
            throw MembershipException.membershipBodyIdNotCorrespondsWithPathId();
        }

        Optional<Membership> existingMembership = membershipService.getMembership(id);
        if(existingMembership.isPresent()) {
            Membership newMembership = this.membershipMapper.membershipUpdatedRequestToMembership(memberRequest);
            existingMembership.get().update(newMembership);
            membershipService.updateMembership(existingMembership.get());
            return ResponseEntity.noContent().build();
        }
        else {
            throw MembershipException.membershipNotFound();
        }
    }

    @ApiOperation(value = "Patch a Membership",
            notes = "Patch the basic details of a membership based on the member ID.")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchMembership(
            @PathVariable String id,
            @RequestBody MembershipAddedRequest memberRequest
    ) {
        Optional<Membership> existingMembership = membershipService.getMembership(id);
        if (existingMembership.isPresent()) {
            Membership newMembership = this.membershipMapper.membershipRequestToMembership(memberRequest);
            existingMembership.get().patch(newMembership);
            membershipService.updateMembership(existingMembership.get());
            return ResponseEntity.noContent().build();
        }
        else {
            throw MembershipException.membershipNotFound();
        }
    }

    /**
     *  Skills:
     */

    @ApiOperation(value = "Add skills to a member",
            notes = "Add some skills from a list to a certain member.")
    @PostMapping("/skills/{id}")
    public ResponseEntity<Void> addSkills(
            @PathVariable String id,
            @RequestBody List<Skills> skillsRequest
    ) {
        Optional<Membership> existingMembership = membershipService.getMembership(id);
        if(existingMembership.isPresent()) {
            membershipService.addSkillsToMember(skillsRequest, id);
            return ResponseEntity.ok().build();
        }
        else {
            throw MembershipException.membershipNotFound();
        }
    }

    @ApiOperation(value = "Get skills of a member",
            notes = "Get all skills of a certain member.")
    @GetMapping("/skills/{id}")
    public ResponseEntity<List<Skills>> getSkillsOfMember(@PathVariable String id) {
        Optional<Membership> membership = membershipService.getMembership(id);
        if(membership.isPresent()) {
            List<Skills> list = membership.get().getSkills();
            if (list.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            else {
                return ResponseEntity.ok(list);
            }
        }
        else {
            throw MembershipException.membershipNotFound();
        }
    }

    /**
     *  Fees:
     */

    @ApiOperation(value = "Pay a membership fee of a member",
            notes = "Add membership fee to the DB.")
    @PostMapping("/fees/{id}")
    public ResponseEntity<Void> addMembershipFee(
            @PathVariable String id,
            @RequestBody Double paidInAmount
            ) {
        MembershipFeeModel newFeeAdded = this.membershipFeeMapper.membershipFeeRequestToMembershipFeeModel(paidInAmount);
        Optional<Membership> existingMembership = membershipService.getMembership(id);
        if(existingMembership.isPresent()) {
            membershipService.addFeeToMember(newFeeAdded, existingMembership.get());
            return ResponseEntity.ok().build();
        }
        else {
            throw MembershipException.membershipNotFound();
        }
    }

    @ApiOperation(value = "Fetch fees of a member",
            notes = "Get the history of membership fee payments of a member.")
    @GetMapping("/fees/{id}")
    public ResponseEntity<List<MembershipFeeModel>> getFeesOfMember(@PathVariable String id) {
        Optional<Membership> membership = membershipService.getMembership(id);
        if (membership.isPresent()) {
            List<MembershipFeeModel> list = membership.get().getPaidInFeeDetails();
            if (list.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            else {
                return ResponseEntity.ok(list);
            }
        }
        else {
            throw MembershipException.membershipNotFound();
        }
    }
}
