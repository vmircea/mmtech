package com.membership.hub.repository.projects;

import com.membership.hub.exception.ProjectException;
import com.membership.hub.model.project.ProjectModel;
import com.membership.hub.model.project.ProjectStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class ProjectRepository {
    private NamedParameterJdbcTemplate template;

    @Autowired
    public void setDataSource(final DataSource dataSource){
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    public ProjectModel save(ProjectModel project) {
        String sqlCreate =
                "INSERT INTO projects (project_id, name, status, description, amount) \n" +
                "VALUES (:project_id, :name, :status, :description, :amount);";
        String sqlUpdate = "UPDATE projects SET status=:status, name=:name, description=:description WHERE project_id=:project_id";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("project_id", project.getId())
                .addValue("name", project.getName())
                .addValue("status", project.getStatus().toString())
                .addValue("description", project.getDescription())
                .addValue("amount", project.getAmount());

        int count = template.update(sqlUpdate, parameters);
        if (count == 0) {
            count = template.update(sqlCreate, parameters);
            if (count == 0) {
                throw ProjectException.noProjectWasAdded();
            }
        }
        return project;
    }

    public Optional<ProjectModel> findById(String projectId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("project_id", projectId);
        String sqlFind = "SELECT project_id, name, status, description, amount FROM projects WHERE project_id=:project_id";
        List<ProjectModel> fetchedProject = template.query(sqlFind, parameters, projectMapper);
        return getProjectFromResultSet(fetchedProject);
    }

    public List<ProjectModel> findAll() {
        String sql = "SELECT project_id, name, status FROM projects;";
        return template.query(sql, projectMapperForFetchAll);
    }

    private Optional<ProjectModel> getProjectFromResultSet(List<ProjectModel> projects) {
        if (projects  != null && !projects.isEmpty()) {
            return Optional.of(projects.get(0));
        }
        else {
            return Optional.empty();
        }
    }

    private final RowMapper<ProjectModel> projectMapperForFetchAll = ((resultSet, i) -> new ProjectModel(
            resultSet.getString("project_id"),
            resultSet.getString("name"),
            ProjectStatus.valueOf(resultSet.getString("status"))
    ));


    private final RowMapper<ProjectModel> projectMapper = ((resultSet, i) -> new ProjectModel(
            resultSet.getString("project_id"),
            resultSet.getString("name"),
            ProjectStatus.valueOf(resultSet.getString("status")),
            resultSet.getString("description"),
            resultSet.getDouble("amount")
    ));

    public Optional<String> delete(String id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("project_id", id);
        String sqlDelete = "DELETE FROM projects WHERE project_id=:project_id";
        int count = template.update(sqlDelete, parameters);
        if (count != 1) {
            return Optional.empty();
        }
        else {
            return Optional.of(id);
        }
    }
}
