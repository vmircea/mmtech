package com.membership.hub.service.unit;

import com.membership.hub.exception.MembershipException;
import com.membership.hub.exception.ProjectException;
import com.membership.hub.model.membership.MemberProfession;
import com.membership.hub.model.membership.MemberStatus;
import com.membership.hub.model.membership.Membership;
import com.membership.hub.model.project.ProjectModel;
import com.membership.hub.model.project.ProjectStatus;
import com.membership.hub.model.shared.Skills;
import com.membership.hub.repository.projects.ProjectMembersRepository;
import com.membership.hub.repository.projects.ProjectRepository;
import com.membership.hub.repository.projects.ProjectSkillsRepository;
import com.membership.hub.service.MembershipService;
import com.membership.hub.service.ProjectService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectSkillsRepository projectSkillsRepository;
    @Mock
    private ProjectMembersRepository projectMembersRepository;
    @Mock
    private MembershipService membershipService;

    @InjectMocks
    private ProjectService service;

    private static ProjectModel project;
    private static ProjectModel expectedProject;
    private static List<ProjectModel> projectsInDataBase;
    private static List<Skills> skills;
    private static Optional<Membership> existingMembership;
    private static String projectId;
    private static List<String> membersId;

    @BeforeAll
    public static void setup() {
        projectsInDataBase = new ArrayList<>();
        projectsInDataBase.add(new ProjectModel(
                "PROJECT-0001",
                "Project Test",
                ProjectStatus.INPROGRESS,
                "Description",
                null,
                0.0));
        projectsInDataBase.add(new ProjectModel(
                "PROJECT-0003",
                "Project Test",
                ProjectStatus.COMPLETED,
                "Description",
                null,
                0.0));
        project = new ProjectModel(
                "PROJECT-0001",
                "Project Test",
                ProjectStatus.INPROGRESS,
                "Description",
                null,
                0.0);
        expectedProject = new ProjectModel(
                "PROJECT-0001",
                "Project Test",
                ProjectStatus.INPROGRESS,
                "Description",
                null,
                0.0);
        skills = new ArrayList<>();
        skills.add(Skills.CODING);
        skills.add(Skills.DESIGN);
        projectId = "PROJECT-0001";

        existingMembership = Optional.of(new Membership(
                "4e48d7a8-64b9-4455-a37f-903fd62def32",
                "Costel",
                "XX-YYY-000",
                10,
                MemberStatus.ACTIVE,
                MemberProfession.DOCTOR,
                null
        ));

        membersId = new ArrayList<>();
        membersId.add("4e48d7a8-64b9-4455-a37f-903fd62def32");
    }

    @Test
    public void addNewProjectWhenAlreadyExistsWithSameId() {
        when(projectRepository.findAll()).thenReturn(projectsInDataBase);
        ProjectException exception = assertThrows(ProjectException.class, () -> service.addNewProject(project));
        assertEquals(ProjectException.ProjectErrors.PROJECT_ALREADY_EXISTS_WITH_SAME_ID, exception.getError());
    }

    @Test
    public void addNewProjectHappyFlow() {
        ProjectModel projectGood = new ProjectModel(
                "PROJECT-0002",
                "Project Test",
                ProjectStatus.INPROGRESS,
                "Description",
                skills,
                0.0);
        when(projectRepository.findAll()).thenReturn(projectsInDataBase);
        when(projectRepository.save(projectGood)).thenReturn(projectGood);

        ProjectModel result = service.addNewProject(projectGood);

        assertEquals(projectGood.getId(), result.getId());
        assertEquals(projectGood.getName(), result.getName());
        assertEquals(projectGood.getDescription(), result.getDescription());
        assertEquals(projectGood.getSkills(), result.getSkills());
        assertEquals(projectGood.getStatus(), result.getStatus());

        verify(projectSkillsRepository, times(1)).save(skills.get(0), projectGood.getId());
        verify(projectSkillsRepository, times(1)).save(skills.get(1), projectGood.getId());
    }

    @Test
    public void getProjectWhenNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        Optional<ProjectModel> result = service.getProjectById(projectId);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getProjectByIdHappyFlow() {
        List<Membership> members = new ArrayList<>();
        members.add(existingMembership.get());

        expectedProject.setProjectMembers(members);
        expectedProject.setSkills(skills);

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(projectMembersRepository.findMembersIdOfProject(project.getId())).thenReturn(membersId);
        when(membershipService.getMembership(membersId.get(0))).thenReturn(existingMembership);
        when(projectSkillsRepository.findById(project.getId())).thenReturn(skills);

        Optional<ProjectModel> result = service.getProjectById(project.getId());

        assertEquals(expectedProject.getId(), result.get().getId());
        assertEquals(expectedProject.getName(), result.get().getName());
        assertEquals(expectedProject.getStatus(), result.get().getStatus());
        assertEquals(expectedProject.getDescription(), result.get().getDescription());
        assertEquals(expectedProject.getSkills(), result.get().getSkills());
        assertEquals(expectedProject.getProjectMembers(), result.get().getProjectMembers());
    }

    @Test
    public void getProjectsTest() {
        List<ProjectModel> expectedProjects = new ArrayList<>();
        expectedProjects.add(project);

        when(projectRepository.findAll()).thenReturn(projectsInDataBase);

        List<ProjectModel> result = service.getProjects();

        assertEquals(expectedProjects.get(0).getId(), result.get(0).getId());
        assertEquals(expectedProjects.get(0).getName(), result.get(0).getName());
        assertEquals(expectedProjects.get(0).getStatus(), result.get(0).getStatus());
        assertEquals(expectedProjects.get(0).getDescription(), result.get(0).getDescription());
    }

    @Test
    public void updateProjectTest() {
        project.setSkills(skills);

        service.updateProject(project);

        verify(projectRepository, times(1)).save(project);
        verify(projectSkillsRepository, times(1)).save(skills.get(0), project.getId());
        verify(projectSkillsRepository, times(1)).save(skills.get(1), project.getId());
    }

    @Test
    public void deleteProjectTest() {
        when(projectRepository.delete(project.getId())).thenReturn(Optional.of(project.getId()));

        Optional<String> result = service.deleteProject(project.getId());

        assertEquals(project.getId(), result.get());
    }

    @Test
    public void addMemberToProjectWhenMembershipNotFound() {
        when(membershipService.getMembership(existingMembership.get().getId())).thenReturn(Optional.empty());

        MembershipException exception = assertThrows(MembershipException.class, () -> service.addMemberToProject(existingMembership.get().getId(), project.getId()));

        assertEquals(MembershipException.MembershipErrors.MEMBERSHIP_NOT_FOUND, exception.getError());
    }

    @Test
    public void addMemberToProjectWhenProjectNotFound() {
        when(membershipService.getMembership(existingMembership.get().getId())).thenReturn(existingMembership);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());

        ProjectException exception = assertThrows(ProjectException.class, () ->
                service.addMemberToProject(existingMembership.get().getId(), project.getId()));

        assertEquals(ProjectException.ProjectErrors.PROJECT_NOT_FOUND, exception.getError());
    }

    @Test
    public void addMemberToProjectWhenProjectIsCompletedOrOnHold() {
        when(membershipService.getMembership(existingMembership.get().getId())).thenReturn(existingMembership);
        when(projectRepository.findById(projectsInDataBase.get(1).getId())).thenReturn(Optional.of(projectsInDataBase.get(1)));
        when(projectMembersRepository.findMembersIdOfProject(projectsInDataBase.get(1).getId())).thenReturn(membersId);
        when(membershipService.getMembership(membersId.get(0))).thenReturn(existingMembership);
        when(projectSkillsRepository.findById(projectsInDataBase.get(1).getId())).thenReturn(skills);

        ProjectException exception = assertThrows(ProjectException.class, () ->
                service.addMemberToProject(existingMembership.get().getId(), projectsInDataBase.get(1).getId()));

        assertEquals(ProjectException.ProjectErrors.PROJECT_STATE_NOT_OPENED_FOR_MEMBERS, exception.getError());
    }

    @Test
    public void addMemberToProjectWhenMemberDoesNotHaveRequiredSkills() {
        List<Skills> memberSkills = new ArrayList<>();
        memberSkills.add(Skills.INNOVATION);
        existingMembership.get().setSkills(memberSkills);

        // in addMemberToProject
        when(membershipService.getMembership(existingMembership.get().getId())).thenReturn(existingMembership);
        // in getProjectById
        when(projectRepository.findById(projectsInDataBase.get(0).getId())).thenReturn(Optional.of(projectsInDataBase.get(0)));
        when(projectMembersRepository.findMembersIdOfProject(any())).thenReturn(new ArrayList<>());
        when(projectSkillsRepository.findById(projectsInDataBase.get(0).getId())).thenReturn(skills);

        ProjectException exception = assertThrows(ProjectException.class, () ->
                service.addMemberToProject(existingMembership.get().getId(), projectsInDataBase.get(0).getId()));

        assertEquals(ProjectException.ProjectErrors.MEMBER_DOES_NOT_HAVE_REQUIRED_SKILLS, exception.getError());
    }

    @Test
    public void addMemberToProjectWhenMemberWasAlreadyAdded() {
        List<Skills> memberSkills = new ArrayList<>();
        memberSkills.add(Skills.CODING);
        existingMembership.get().setSkills(memberSkills);

        // in addMemberToProject
        when(membershipService.getMembership(existingMembership.get().getId())).thenReturn(existingMembership);
        // in getProjectById
        when(projectRepository.findById(projectsInDataBase.get(0).getId())).thenReturn(Optional.of(projectsInDataBase.get(0)));
        when(projectMembersRepository.findMembersIdOfProject(any())).thenReturn(new ArrayList<>());
        when(projectSkillsRepository.findById(projectsInDataBase.get(0).getId())).thenReturn(skills);
        when(projectMembersRepository.save(existingMembership.get().getId(), projectsInDataBase.get(0).getId()))
                .thenReturn(false);

        ProjectException exception = assertThrows(ProjectException.class, ()
            -> service.addMemberToProject(existingMembership.get().getId(), projectsInDataBase.get(0).getId()));

        assertEquals(ProjectException.ProjectErrors.MEMBER_ALREADY_WORKS_FOR_PROJECT, exception.getError());
    }

    @Test
    public void addMemberToProjectHappyFlow() {
        List<Skills> memberSkills = new ArrayList<>();
        memberSkills.add(Skills.CODING);
        existingMembership.get().setSkills(memberSkills);
        String expectedResult = String.format(
                "Member with name %s was added to project with name %s",
                existingMembership.get().getName(),
                projectsInDataBase.get(0).getName()
        );

        // in addMemberToProject
        when(membershipService.getMembership(existingMembership.get().getId())).thenReturn(existingMembership);
        // in getProjectById
        when(projectRepository.findById(projectsInDataBase.get(0).getId())).thenReturn(Optional.of(projectsInDataBase.get(0)));
        when(projectMembersRepository.findMembersIdOfProject(any())).thenReturn(new ArrayList<>());
        when(projectSkillsRepository.findById(projectsInDataBase.get(0).getId())).thenReturn(skills);
        when(projectMembersRepository.save(existingMembership.get().getId(), projectsInDataBase.get(0).getId()))
                .thenReturn(true);

        String result = service.addMemberToProject(existingMembership.get().getId(), projectsInDataBase.get(0).getId());

        assertEquals(expectedResult, result);
    }



}
