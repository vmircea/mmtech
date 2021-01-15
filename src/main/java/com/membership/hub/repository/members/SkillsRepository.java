package com.membership.hub.repository.members;

import com.membership.hub.model.shared.Skills;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SkillsRepository {
    private NamedParameterJdbcTemplate template;

    @Autowired
    public void setDataSource(final DataSource dataSource){
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<Skills> findById(String id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("member_id", id);
        String sqlFetch =
                "SELECT skillName " +
                "FROM memberskills " +
                "WHERE member_id =:member_id";
        return template.query(sqlFetch, parameters, skillsMapper);
    }

    public void save(Skills skill, String id) {
        String sqlUpdateSkill =
                "UPDATE memberskills " +
                "SET skillName =:skill, member_id =:member_id " +
                "WHERE member_id =:member_id " +
                "AND skillName =:skill";
        String sqlAddSkill =
                "INSERT INTO memberskills (skillName, member_id) " +
                "VALUES (:skill, :member_id)";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("skill", skill.toString())
                .addValue("member_id", id);

        int count = template.update(sqlUpdateSkill, parameters);
        if (count == 0) {
            template.update(sqlAddSkill, parameters);
        }
    }

    private final RowMapper<Skills> skillsMapper = (resultSet, i) -> {
        String MS = resultSet.getString("skillName");
        return Skills.valueOf(MS);
    };
}
