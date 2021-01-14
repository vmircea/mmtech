package com.membership.hub.repository.projects;

import com.membership.hub.model.branch.BranchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ProjectMembersRepository {
    private NamedParameterJdbcTemplate template;

    @Autowired
    public void setDataSource(final DataSource dataSource){
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    public boolean save(String memberId, String projectId) {
        String sqlUpdate = "UPDATE projectmembers SET project_id=:project_id, member_id=:member_id WHERE member_id=:member_id";
        String sqlSave = "INSERT INTO projectmembers (project_id, member_id) VALUES (:project_id, :member_id)";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("project_id", projectId)
                .addValue("member_id", memberId);

        int count = template.update(sqlUpdate, parameters);
        if (count == 0) {
            count = template.update(sqlSave, parameters);
            return count == 1;
        }
        return count == 0;
    }

    public List<String> findMembersIdOfProject(String projectId) {
        String sqlFind = "SELECT member_id FROM projectmembers WHERE project_id=:project_id";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("project_id", projectId);
        return template.query(sqlFind, parameters, projectMembersMapper);
    }

    private final RowMapper<String> projectMembersMapper = (resultSet, i) -> {
        return resultSet.getString("member_id");
    };
}
