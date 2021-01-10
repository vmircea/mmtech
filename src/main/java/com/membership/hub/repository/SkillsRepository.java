package com.membership.hub.repository;

import com.membership.hub.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class SkillsRepository {
    private NamedParameterJdbcTemplate template;

    @Autowired
    public void setDataSource(final DataSource dataSource){
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    public Optional<List<MemberSkill>> findById(String id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("member_id", id);
        String sqlFetch =
                "SELECT skillName " +
                "FROM skills " +
                "WHERE member_id =:member_id";
        List<MemberSkill> list = template.query(sqlFetch, parameters, skillsMapper);
        return list != null ? Optional.of(list) : Optional.empty();
    }

    public void save(MemberSkill skill, String id) {
        String sqlUpdateSkill =
                "UPDATE skills " +
                "SET skillName =:skill, member_id =:member_id " +
                "WHERE member_id =:member_id " +
                "AND skillName =:skill";
        String sqlAddSkill =
                "INSERT INTO skills (skillName, member_id) " +
                "VALUES (:skill, :member_id)";

        MapSqlParameterSource parametersContactInfo = new MapSqlParameterSource()
                .addValue("skill", skill.toString())
                .addValue("member_id", id);

        int count = template.update(sqlUpdateSkill, parametersContactInfo);
        if (count == 0) {
            template.update(sqlAddSkill, parametersContactInfo);
        }
    }

    private final RowMapper<MemberSkill> skillsMapper = (resultSet, i) -> {
        String MS = resultSet.getString("skillName");
        return MemberSkill.valueOf(MS);
    };
}
