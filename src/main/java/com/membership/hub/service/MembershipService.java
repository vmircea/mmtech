package com.membership.hub.service;

import com.membership.hub.exception.MembershipExistsException;
import com.membership.hub.model.MemberSkill;
import com.membership.hub.model.Membership;
import com.membership.hub.model.MembershipFeeModel;
import com.membership.hub.repository.ContactRepository;
import com.membership.hub.repository.FeesRepository;
import com.membership.hub.repository.MembershipRepository;
import com.membership.hub.repository.SkillsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final ContactRepository contactRepository;
    private final SkillsRepository skillsRepository;
    private final FeesRepository feesRepository;

    public MembershipService(MembershipRepository membershipRepository, ContactRepository contactRepository, SkillsRepository skillsRepository, FeesRepository feesRepository) {
        this.membershipRepository = membershipRepository;
        this.contactRepository = contactRepository;
        this.skillsRepository = skillsRepository;
        this.feesRepository = feesRepository;
    }

    public Membership createNewMembership(Membership newMembership) {
        // If already exists, throw MembershipExistsException
        if(membershipRepository.findAll().stream()
                .anyMatch(dataBaseItem ->
                dataBaseItem.getName().equals(newMembership.getName()) ||
                dataBaseItem.getContactInfo().getPhoneNumber().equals(newMembership.getContactInfo().getPhoneNumber()))) {
            throw new MembershipExistsException("This name or phoneName already exists in the database");
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
        List<MemberSkill> skills = this.getSkillsById(id);
        if(skills != null) {
            membership.get().setSkills(skills);
        }
        List<MembershipFeeModel> fees = this.getFeesById(id);
        if(fees != null) {
            membership.get().setPaidInFeeDetails(fees);
        }
        return membership;
    }

    public List<Membership> getMemberships() {
        List<Membership> listMembership = this.membershipRepository.findAll();
        listMembership.forEach(membership -> {
            List<MemberSkill> skills = this.getSkillsById(membership.getId());
            List<MembershipFeeModel> fees = this.getFeesById(membership.getId());
            membership.setSkills(skills);
            membership.setPaidInFeeDetails(fees);
        });
        return listMembership;
    }

    public boolean updateMembership(Membership membership) {
        Optional<Membership> findMembership = this.membershipRepository.findById(membership.getId());

        if(findMembership.isPresent()) {
            contactRepository.save(membership.getContactInfo());
            membershipRepository.save(membership);
            return true;
        }
        return false;
    }

    public void addSkillsToMember(List<MemberSkill> skillsRequest, String id) {
        skillsRequest.forEach(skill -> skillsRepository.save(skill, id));
    }
    public List<MemberSkill> getSkillsById(String id) {
        Optional<List<MemberSkill>> existingSkills = skillsRepository.findById(id);
        return existingSkills.orElse(null);
    }

    public void addFeeToMember(List<MembershipFeeModel> newFeesAdded, String id) {
        newFeesAdded.forEach(fee -> feesRepository.save(fee, id));
    }
    public List<MembershipFeeModel> getFeesById(String id) {
        Optional<List<MembershipFeeModel>> existingFees = feesRepository.findById(id);
        return existingFees.orElse(null);
    }
}
