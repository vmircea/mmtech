package com.membership.hub.controller;

import com.membership.hub.dto.ProjectAddedRequest;
import com.membership.hub.dto.ProjectUpdateRequest;
import com.membership.hub.exception.ProjectException;
import com.membership.hub.mapper.ProjectMapper;
import com.membership.hub.model.project.ProjectModel;
import com.membership.hub.service.ProjectService;
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
@RequestMapping("/projects")
@Api(value = "/projects",
        tags = "Projects")
public class ProjectController {

    private final ProjectMapper projectMapper;
    private final ProjectService projectService;

    public ProjectController(ProjectMapper projectMapper, ProjectService projectService) {
        this.projectMapper = projectMapper;
        this.projectService = projectService;
    }

    @ApiOperation(value = "Add a new Project",
            notes = "Add a new Project based on the information received in the request")
    @PostMapping
    public ResponseEntity<ProjectModel> addNewProject(
            @Valid
            @ApiParam(name = "project", value = "Project Basic details", required = true)
            @RequestBody ProjectAddedRequest request
    ) {
        ProjectModel newProject = this.projectMapper.projectAddedRequestToProjectModel(request);

        ProjectModel addedProject = this.projectService.addNewProject(newProject);
        return ResponseEntity
                .created(URI.create("/projects/" + addedProject.getId()))
                .body(addedProject);
    }

    @ApiOperation(value = "Get a Project By its Id including its members",
            notes = "Fetch a Project based on the project Id which includes the members who work on the project.")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectModel> getProjectById(@PathVariable String id) {
        Optional<ProjectModel> fetchedProject = this.projectService.getProjectById(id);
        if (fetchedProject.isPresent()) {
            return ResponseEntity.ok(fetchedProject.get());
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation(value = "Get all Projects",
            notes = "Fetch all Projects with all details from the DataBase.")
    @GetMapping
    public ResponseEntity<List<ProjectModel>> getProjects() {
        List<ProjectModel> projects = this.projectService.getProjects();

        if(projects.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.ok(projects);
        }
    }

    @ApiOperation(value = "Update a Project By its Id",
            notes = "Update a Project based on the project Id")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProject(
            @PathVariable String id,
            @Valid @RequestBody ProjectUpdateRequest projectUpdateRequest
            ) {
        if (!projectUpdateRequest.getId().equals(id)) {
            throw ProjectException.projectBodyIdNotCorrespondsWithPathId();
        }

        Optional<ProjectModel> existingProject = this.projectService.getProjectById(id);
        if (existingProject.isPresent()) {
            ProjectModel projectFromRequest = this.projectMapper.projectUpdateRequestToProjectModel(projectUpdateRequest);
            existingProject.get().update(projectFromRequest);
            this.projectService.updateProject(existingProject.get());
            return ResponseEntity.noContent().build();
        }
        else {
            throw ProjectException.projectNotFound();
        }
    }

    @ApiOperation(value = "Patch a Project By its Id",
            notes = "Patch a Project based on the project Id")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchProject(
            @PathVariable String id,
            @RequestBody ProjectUpdateRequest projectUpdateRequest
    ) {
        Optional<ProjectModel> existingProject = this.projectService.getProjectById(id);
        if (existingProject.isPresent()) {
            ProjectModel projectFromRequest = this.projectMapper.projectUpdateRequestToProjectModel(projectUpdateRequest);
            existingProject.get().patch(projectFromRequest);
            this.projectService.updateProject(existingProject.get());
            return ResponseEntity.noContent().build();
        }
        else {
            throw ProjectException.projectNotFound();
        }
    }

    @ApiOperation(value = "Delete a Project by ID",
            notes = "Delete a project from the DataBase based on its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable String id) {
        Optional<String> deletedProject = this.projectService.deleteProject(id);
        if (deletedProject.isPresent()) {
            return ResponseEntity.ok(deletedProject.get());
        }
        else {
            throw ProjectException.noProjectDeleted();
        }
    }

    @ApiOperation(value = "Add an existing member to a Project",
            notes = "Add an existing member to an existing Project based on the common skills between the member and the project requirements.")
    @PostMapping("/members")
    public ResponseEntity<String> addMemberToProject(@RequestParam String memberId, @RequestParam String projectId) {
        String toReturn = this.projectService.addMemberToProject(memberId, projectId);
        return ResponseEntity.ok(toReturn);
    }
}
