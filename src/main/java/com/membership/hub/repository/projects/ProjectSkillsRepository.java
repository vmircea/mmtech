package com.membership.hub.repository.projects;

import com.membership.hub.model.shared.Skills;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ProjectSkillsRepository {
    private NamedParameterJdbcTemplate template;

    @Autowired
    public void setDataSource(final DataSource dataSource){
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void save(Skills skill, String projectId) {
        String sqlUpdateSkill =
                        "UPDATE ProjectSkills " +
                        "SET skillName =:skill, project_id =:project_id " +
                        "WHERE project_id =:project_id " +
                        "AND skillName =:skill";
        String sqlAddSkill =
                        "INSERT INTO ProjectSkills (skillName, project_id) " +
                        "VALUES (:skill, :project_id)";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("skill", skill.toString())
                .addValue("project_id", projectId);

        int count = template.update(sqlUpdateSkill, parameters);
        if (count == 0) {
            template.update(sqlAddSkill, parameters);
        }
    }

    public List<Skills> findById(String projectId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("project_id", projectId);
        String sqlFetch = "SELECT skillName FROM ProjectSkills WHERE project_id=:project_id";

        return template.query(sqlFetch, parameters, skillsMapper);
    }

    private final RowMapper<Skills> skillsMapper = (resultSet, i) -> {
        String MS = resultSet.getString("skillName");
        return Skills.valueOf(MS);
    };
}
