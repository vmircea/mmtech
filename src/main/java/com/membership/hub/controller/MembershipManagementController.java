package com.membership.hub.controller;

import com.membership.hub.dto.MembershipAddedRequest;
import com.membership.hub.mapper.MembershipMapper;
import com.membership.hub.model.Membership;
import com.membership.hub.service.MembershipService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api")
public class MembershipManagementController {

    private final MembershipService membershipService;
    private final MembershipMapper membershipMapper;

    public MembershipManagementController(MembershipMapper membershipMapper, MembershipService membershipService) {
        this.membershipMapper = membershipMapper;
        this.membershipService = membershipService;
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
}
