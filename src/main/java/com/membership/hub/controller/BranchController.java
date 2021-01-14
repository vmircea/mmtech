package com.membership.hub.controller;

import com.membership.hub.dto.BranchRequest;
import com.membership.hub.mapper.BranchModelMapper;
import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.service.BranchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "/branch",
        tags = "Branches")
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
    @ApiOperation(value = "Create a new Branch",
            notes = "Create a new branch based on the information received in the request")
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

    @ApiOperation(value = "Get a Branch by ID",
            notes = "Fetch a branch with all details from the DataBase based on its ID")
    @GetMapping("/{id}")
    public ResponseEntity<BranchModel> getBranch(@PathVariable String id) {
        Optional<BranchModel> existingBranch = branchService.getBranch(id);

        return existingBranch.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ApiOperation(value = "Get all Branches details from DB",
            notes = "Fetch all branches with all details from the DataBase.")
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

    @ApiOperation(value = "Delete a Branch by ID",
            notes = "Delete a branch from the DataBase based on its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<BranchModel> deleteBranch(@PathVariable String id) {
        Optional<BranchModel> existingBranch = branchService.deleteBranch(id);

        return existingBranch.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
