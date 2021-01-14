package com.membership.hub.exception;

public class ProjectException extends RuntimeException{

    public enum ProjectErrors {
        PROJECT_BODY_ID_NOT_EQUAL_WITH_PATH_VARIABLE_ID,
        PROJECT_NOT_FOUND,
        PROJECT_WITH_THIS_ID_NOT_DELETED,
        PROJECT_NOT_ADDED,
        MEMBER_ALREADY_WORKS_FOR_PROJECT,
        PROJECT_ALREADY_EXISTS_WITH_SAME_ID,
        MEMBER_DOES_NOT_HAVE_REQUIRED_SKILLS,
        PROJECT_STATE_NOT_OPENED_FOR_MEMBERS
    }

    private ProjectErrors error;

    private ProjectException(ProjectErrors error) {
        this.error = error;
    }

    public ProjectErrors getError() {
        return error;
    }

    @Override
    public String toString() {
        return error.name().toUpperCase();
    }

    public static ProjectException projectBodyIdNotCorrespondsWithPathId() {
        return new ProjectException(ProjectErrors.PROJECT_BODY_ID_NOT_EQUAL_WITH_PATH_VARIABLE_ID);
    }

    public static ProjectException projectNotFound() {
        return  new ProjectException(ProjectErrors.PROJECT_NOT_FOUND);
    }

    public static  ProjectException noProjectDeleted() {
        return new ProjectException(ProjectErrors.PROJECT_WITH_THIS_ID_NOT_DELETED);
    }

    public static  ProjectException noProjectWasAdded() {
        return new ProjectException(ProjectErrors.PROJECT_NOT_ADDED);
    }

    public static  ProjectException noMemberWasAddedBecauseAlreadyExists() {
        return new ProjectException(ProjectErrors.MEMBER_ALREADY_WORKS_FOR_PROJECT);
    }

    public static  ProjectException projectAlreadyExists() {
        return new ProjectException(ProjectErrors.PROJECT_ALREADY_EXISTS_WITH_SAME_ID);
    }

    public static  ProjectException memberDoesNotHaveRequiredSkills() {
        return new ProjectException(ProjectErrors.MEMBER_DOES_NOT_HAVE_REQUIRED_SKILLS);
    }

    public static  ProjectException projectStateIsNotOpenForMembers() {
        return new ProjectException(ProjectErrors.PROJECT_STATE_NOT_OPENED_FOR_MEMBERS);
    }
}
