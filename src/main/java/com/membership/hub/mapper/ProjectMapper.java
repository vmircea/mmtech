package com.membership.hub.mapper;

import com.membership.hub.dto.ProjectAddedRequest;
import com.membership.hub.dto.ProjectUpdateRequest;
import com.membership.hub.model.project.ProjectModel;
import com.membership.hub.model.project.ProjectStatus;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {
    public ProjectModel projectAddedRequestToProjectModel(ProjectAddedRequest projectAddedRequest) {
        return new ProjectModel(
                projectAddedRequest.getId(),
                projectAddedRequest.getName(),
                ProjectStatus.NEW,
                projectAddedRequest.getDescription(),
                projectAddedRequest.getSkills(),
                0.0
        );
    }

    public ProjectModel projectUpdateRequestToProjectModel(ProjectUpdateRequest projectUpdateRequest) {
        return new ProjectModel(
                projectUpdateRequest.getId(),
                projectUpdateRequest.getName(),
                projectUpdateRequest.getStatus(),
                projectUpdateRequest.getDescription(),
                projectUpdateRequest.getSkills()
        );
    }
}
