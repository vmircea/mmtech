package com.membership.hub.model.project;

import com.membership.hub.model.membership.Membership;
import com.membership.hub.model.shared.Skills;

import java.util.List;

public class ProjectModel {
    private String id;
    private String name;
    private ProjectStatus status;
    private String description;
    private List<Skills> skills;
    private Double amount;
    private List<Membership> projectMembers;

    public ProjectModel(String id, String name, ProjectStatus status, String description, List<Skills> skills, Double amount) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.skills = skills;
        this.amount = amount;
    }

    public ProjectModel(String id, String name, ProjectStatus status, String description, Double amount) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.amount = amount;
    }

    public ProjectModel(String id, String name, ProjectStatus status, String description, List<Skills> skills) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.skills = skills;
    }

    public ProjectModel(String id, String name, ProjectStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public Double getAmount() {
        return amount;
    }

    public List<Skills> getSkills() {
        return skills;
    }

    public void setSkills(List<Skills> skills) {
        this.skills = skills;
    }

    public List<Membership> getProjectMembers() {
        return projectMembers;
    }

    public void setProjectMembers(List<Membership> projectMembers) {
        this.projectMembers = projectMembers;
    }

    public void update (ProjectModel project) {
        if (project != null) {
            name = project.getName();
            status = project.getStatus();
            description = project.getDescription();
            skills =project.getSkills();
        }
    }

    public void patch (ProjectModel project) {
        if (project != null) {
            if (project.getName() != null) {
                name = project.getName();
            }
            if (project.getStatus() != null) {
                status = project.getStatus();
            }
            if (project.getDescription() != null) {
                description = project.getDescription();
            }
            if (project.getSkills() != null) {
                skills =project.getSkills();
            }
        }
    }
}