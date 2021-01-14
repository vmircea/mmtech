package com.membership.hub.service;

import com.membership.hub.exception.BranchException;
import com.membership.hub.exception.MembershipException;
import com.membership.hub.mapper.PaymentModelFactory;
import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.model.shared.Skills;
import com.membership.hub.model.membership.Membership;
import com.membership.hub.model.membership.MembershipFeeModel;
import com.membership.hub.model.shared.PaymentModel;
import com.membership.hub.repository.*;
import com.membership.hub.repository.members.MembershipRepository;
import com.membership.hub.repository.members.SkillsRepository;
import com.membership.hub.repository.shared.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final ContactRepository contactRepository;
    private final SkillsRepository skillsRepository;
    private final BranchRepository branchRepository;
    private final PaymentsRepository paymentsRepository;
    private final PaymentModelFactory paymentModelFactory;

    public MembershipService(MembershipRepository membershipRepository, ContactRepository contactRepository, SkillsRepository skillsRepository, BranchRepository branchRepository, PaymentsRepository paymentsRepository, PaymentModelFactory paymentModelFactory) {
        this.membershipRepository = membershipRepository;
        this.contactRepository = contactRepository;
        this.skillsRepository = skillsRepository;
        this.branchRepository = branchRepository;
        this.paymentsRepository = paymentsRepository;
        this.paymentModelFactory = paymentModelFactory;
    }

    public Membership createNewMembership(Membership newMembership) {
        // If already exists, throw MembershipExistsException
        if(membershipRepository.findAll().stream()
                .anyMatch(dataBaseItem ->
                dataBaseItem.getName().equals(newMembership.getName()) ||
                dataBaseItem.getContactInfo().getPhoneNumber().equals(newMembership.getContactInfo().getPhoneNumber()))) {
            throw MembershipException.membershipAlreadyExists();
        }
        // Save ContactInfo to DB
        int addedContactInfoId = contactRepository.save(newMembership.getContactInfo()).getId();

        newMembership.setId(UUID.randomUUID().toString());
        newMembership.getContactInfo().setId(addedContactInfoId);
        // Save Membership which contains ContactInfo and ... and then return what created.
        return membershipRepository.save(newMembership);
    }

    public Optional<Membership> getMembership(String id) {
        Optional<Membership> membership = this.membershipRepository.findById(id);
        if(membership.isEmpty()) {
            throw MembershipException.membershipNotFound();
        }
        List<Skills> skills = this.getSkillsById(id);
        if(!skills.isEmpty()) {
            membership.get().setSkills(skills);
        }
        List<MembershipFeeModel> fees = this.getFeesById(id);
        if(!fees.isEmpty()) {
            membership.get().setPaidInFeeDetails(fees);
        }
        return membership;
    }

    public List<Membership> getMemberships() {
        List<Membership> listMembership = this.membershipRepository.findAll();
        return addSkillsAndFees(listMembership);
    }

    public List<Membership> getMembershipsByBranchId(String id) {
        List<Membership> listMembership = membershipRepository.findAllByBranch(id);
        return addSkillsAndFees(listMembership);
    }

    public void updateMembership(Membership membership) {
        contactRepository.save(membership.getContactInfo());
        membershipRepository.save(membership);
    }

    public void addSkillsToMember(List<Skills> skillsRequest, String id) {
        skillsRequest.forEach(skill -> skillsRepository.save(skill, id));
    }
    public List<Skills> getSkillsById(String id) {
        return skillsRepository.findById(id);
    }

    public void addFeeToMember(MembershipFeeModel newFeeAdded, Membership memberWhoPaysFees) {
        String branchId = memberWhoPaysFees.getBranchId();
        Optional<BranchModel> existingBranch = branchRepository.findById(branchId);
        if (existingBranch.isPresent()) {
            String description = String.format(
                    "Paid monthly membership by %s into branch %s",
                    memberWhoPaysFees.getName(),
                    existingBranch.get().getBranchName());
            PaymentModel memberToBranchTransaction = paymentModelFactory.createPaymentModel(
                    memberWhoPaysFees.getId(),
                    branchId,
                    newFeeAdded.getPaidInAmount(),
                    newFeeAdded.getPaidInDate(),
                    description);
            paymentsRepository.save(memberToBranchTransaction);
            branchRepository.updateAmount(branchId, memberToBranchTransaction.getAmount());
        }
        else {
            throw BranchException.branchNotFound();
        }
    }
    public List<MembershipFeeModel> getFeesById(String id) {
        return paymentsRepository.findAllMembershipFeesById(id);
    }

    private List<Membership> addSkillsAndFees(List<Membership> listMembership) {
        listMembership.forEach(membership -> {
            List<Skills> skills = this.getSkillsById(membership.getId());
            List<MembershipFeeModel> fees = this.getFeesById(membership.getId());
            membership.setSkills(skills);
            membership.setPaidInFeeDetails(fees);
        });
        return  listMembership;
    }
}
