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
        List<Skills> fetchedSkills = this.projectSkillsRepository.findById(projectId);
        if (fetchedProject.isPresent()) {
            List<String> membersId = this.projectMembersRepository.findMembersIdOfProject(projectId);
            List<Membership> membersOfProject = new ArrayList<>();
            membersId.forEach(memberId -> {
                Optional<Membership> existingMembership = this.membershipService.getMembership(memberId);
                if (existingMembership.isPresent()) {
                    membersOfProject.add(existingMembership.get());
                }
            });
            fetchedProject.get().setSkills(fetchedSkills);
            fetchedProject.get().setProjectMembers(membersOfProject);
            return fetchedProject;
        }
        else {
            return Optional.empty();
        }
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
        Optional<Membership> existingMembership = this.membershipService.getMembership(memberId);
        if (existingMembership.isPresent()) {
            Optional<ProjectModel> existingProject = getProjectById(projectId);
            if (existingProject.isPresent()) {
                if (!existingProject.get().getStatus().equals(ProjectStatus.COMPLETED) && !existingProject.get().getStatus().equals(ProjectStatus.ONHOLD)) {
                    boolean memberHasRequiredSkills = existingMembership.get().getSkills().stream()
                            .anyMatch(eachSkillOfMember -> existingProject.get().getSkills().contains(eachSkillOfMember));
                    if (memberHasRequiredSkills) {
                        if (this.projectMembersRepository.save(memberId, projectId)) {
                            String toReturn = String.format(
                                    "Member with name %s was added to project with name %s",
                                    existingMembership.get().getName(),
                                    existingProject.get().getName());
                            return toReturn;
                        }
                        else {
                            throw ProjectException.noMemberWasAddedBecauseAlreadyExists();
                        }
                    }
                    else {
                        throw ProjectException.memberDoesNotHaveRequiredSkills();
                    }
                }
                else {
                    throw ProjectException.projectStateIsNotOpenForMembers();
                }
            }
            else {
                throw ProjectException.projectNotFound();
            }
        }
        else {
            throw MembershipException.membershipNotFound();
        }
    }
}
