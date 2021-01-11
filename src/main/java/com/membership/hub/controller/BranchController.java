package com.membership.hub.controller;

import com.membership.hub.dto.BranchRequest;
import com.membership.hub.mapper.BranchModelMapper;
import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.service.BranchService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/branch")
public class BranchController {
    private final BranchModelMapper branchModelMapper;
    private final BranchService branchService;

    public BranchController(BranchModelMapper branchModelMapper, BranchService branchService) {
        this.branchModelMapper = branchModelMapper;
        this.branchService = branchService;
    }

    /**
     * Branch ENDPOINTS: newBranch, getBranch, new payments, get payments
     */
    @PostMapping
    public ResponseEntity<BranchModel> createBranch(
            @Valid
            @RequestBody BranchRequest branchRequest
            ) {
        BranchModel newBranch = this.branchModelMapper.branchRequestToBranchModel(branchRequest);

        BranchModel savedBranch = this.branchService.createNewBranch(newBranch);
        return ResponseEntity
                .created(URI.create("/branch/" + savedBranch.getBranchId()))
                .body(savedBranch);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchModel> getBranch(@PathVariable String id) {
        Optional<BranchModel> existingBranch = branchService.getBranch(id);

        return existingBranch.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<BranchModel>> getBranches() {
        List<BranchModel> branchesList = branchService.getBranches();

        if (branchesList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.ok(branchesList);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BranchModel> deleteBranch(@PathVariable String id) {
        Optional<BranchModel> existingBranch = branchService.deleteBranch(id);

        return existingBranch.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
