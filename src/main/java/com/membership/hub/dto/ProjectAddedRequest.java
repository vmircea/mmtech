package com.membership.hub.dto;

import com.membership.hub.model.shared.Skills;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.membership.hub.model.shared.Pattern.PROJECT_ID;

public class ProjectAddedRequest {
    @NotNull
    @Pattern(regexp = PROJECT_ID, message = "not a valid project id")
    private String id;
    @NotNull
    private String name;
    private String description;
    @NotEmpty
    private List<Skills> skills;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Skills> getSkills() {
        return skills;
    }
}
