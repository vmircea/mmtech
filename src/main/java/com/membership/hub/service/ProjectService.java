package com.membership.hub.service;

import com.membership.hub.exception.MembershipException;
import com.membership.hub.exception.ProjectException;
import com.membership.hub.model.membership.Membership;
import com.membership.hub.model.project.ProjectModel;
import com.membership.hub.model.project.ProjectStatus;
import com.membership.hub.model.shared.Skills;
import com.membership.hub.repository.projects.ProjectMembersRepository;
import com.membership.hub.repository.projects.ProjectRepository;
import com.membership.hub.repository.projects.ProjectSkillsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectSkillsRepository projectSkillsRepository;
    private final ProjectMembersRepository projectMembersRepository;
    private final MembershipService membershipService;

    public ProjectService(ProjectRepository projectRepository, ProjectSkillsRepository projectSkillsRepository, ProjectMembersRepository projectMembersRepository, MembershipService membershipService) {
        this.projectRepository = projectRepository;
        this.projectSkillsRepository = projectSkillsRepository;
        this.projectMembersRepository = projectMembersRepository;
        this.membershipService = membershipService;
    }

    public ProjectModel addNewProject(ProjectModel newProject) {
        if (projectRepository.findAll().stream().anyMatch(project -> project.getId().equals(newProject.getId()))) {
            throw ProjectException.projectAlreadyExists();
        }
        ProjectModel addedProject = this.projectRepository.save(newProject);
        List<Skills> projectSkills = newProject.getSkills();
        projectSkills.forEach(skill -> this.projectSkillsRepository.save(skill, newProject.getId()));
        return addedProject;
    }

    public Optional<ProjectModel> getProjectById(String projectId) {
        Optional<ProjectModel> fetchedProject = this.projectRepository.findById(projectId);
        if (fetchedProject.isEmpty()) {
            return Optional.empty();
        }
        // Fetch members who work for this project to a list.
        List<String> membersId = this.projectMembersRepository.findMembersIdOfProject(projectId);
        List<Membership> membersOfProject = new ArrayList<>();
        membersId.forEach(memberId -> {
            Optional<Membership> existingMembership = this.membershipService.getMembership(memberId);
            existingMembership.ifPresent(membersOfProject::add);
        });
        // Fetch skills required for this project.
        List<Skills> fetchedSkills = this.projectSkillsRepository.findById(projectId);
        // Add members and skills to the ProjectModel.
        fetchedProject.get().setSkills(fetchedSkills);
        fetchedProject.get().setProjectMembers(membersOfProject);
        return fetchedProject;
    }

    public List<ProjectModel> getProjects() {
        return this.projectRepository.findAll();
    }

    public void updateProject(ProjectModel project) {
        this.projectRepository.save(project);
        List<Skills> skills = project.getSkills();
        skills.forEach(skill -> this.projectSkillsRepository.save(skill, project.getId()));
    }

    public Optional<String> deleteProject(String id) {
        return this.projectRepository.delete(id);
    }

    public String addMemberToProject(String memberId, String projectId) {
        Optional<Membership> existingMembership = membershipService.getMembership(memberId);
        if (existingMembership.isEmpty()) {
            throw MembershipException.membershipNotFound();
        }
        Optional<ProjectModel> existingProject = this.getProjectById(projectId);
        if (existingProject.isEmpty()) {
            throw ProjectException.projectNotFound();
        }
        if (existingProject.get().getStatus().equals(ProjectStatus.COMPLETED)
           || existingProject.get().getStatus().equals(ProjectStatus.ONHOLD)) {
            throw ProjectException.projectStateIsNotOpenForMembers();
        }
        boolean memberHasRequiredSkills = existingMembership.get().getSkills().stream()
                .anyMatch(eachSkillOfMember -> existingProject.get().getSkills().contains(eachSkillOfMember));
        if (!memberHasRequiredSkills) {
            throw ProjectException.memberDoesNotHaveRequiredSkills();
        }
        if (!this.projectMembersRepository.save(memberId, projectId)) {
            throw ProjectException.noMemberWasAddedBecauseAlreadyExists();
        }
        return String.format(
                "Member with name %s was added to project with name %s",
                existingMembership.get().getName(),
                existingProject.get().getName());
    }
}
