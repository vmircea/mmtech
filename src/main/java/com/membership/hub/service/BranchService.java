package com.membership.hub.service;

import com.membership.hub.exception.BranchException;
import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.model.shared.ContactInfo;
import com.membership.hub.repository.BranchRepository;
import com.membership.hub.repository.shared.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchService {
    private final ContactRepository contactRepository;
    private final BranchRepository branchRepository;

    public BranchService(ContactRepository contactRepository, BranchRepository branchRepository) {
        this.contactRepository = contactRepository;
        this.branchRepository = branchRepository;
    }

    public BranchModel createNewBranch(BranchModel newBranch) {
        if (branchRepository.findAll().stream().anyMatch(existingBranch -> existingBranch.getBranchId().equals(newBranch.getBranchId()))) {
            throw BranchException.branchAlreadyExistsWithThisId();
        }
        ContactInfo savedContactInfo = this.contactRepository.save(newBranch.getContactInfo());
        newBranch.setContactInfo(savedContactInfo);
        return this.branchRepository.save(newBranch);
    }

    public Optional<BranchModel> getBranch(String id) {
        return branchRepository.findById(id);
    }

    public List<BranchModel> getBranches() {
        return branchRepository.findAll();
    }

    public Optional<BranchModel> deleteBranch(String id) {
        return branchRepository.deleteBranch(id);
    }
}
