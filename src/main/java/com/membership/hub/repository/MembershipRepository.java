package com.membership.hub.repository;

import com.membership.hub.model.shared.ContactInfo;
import com.membership.hub.model.membership.MemberProfession;
import com.membership.hub.model.membership.MemberStatus;
import com.membership.hub.model.membership.Membership;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class MembershipRepository {
    private NamedParameterJdbcTemplate template;

    @Autowired
    public void setDataSource(final DataSource dataSource){
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<Membership> findAll() {
        String sql =
                "SELECT m.member_id, m.branch_id, m.name, m.age, m.status, m.profession, m.contactInfo_id, c.phoneNumber, c.emailAddress, c.country, c.city, c.street, c.building\n" +
                "FROM membership m\n" +
                "JOIN contactinfo c ON (m.contactInfo_id = c.id)";

        return template.query(sql, basicMembershipMapper);
    }

    public List<Membership> findAllByBranch(String branch_id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("branch_id", branch_id);
        String sql =
                "SELECT m.member_id, m.branch_id, m.name, m.age, m.status, m.profession, m.contactInfo_id, c.phoneNumber, c.emailAddress, c.country, c.city, c.street, c.building\n" +
                "FROM membership m\n" +
                "JOIN contactinfo c ON (m.contactInfo_id = c.id) " +
                "WHERE m.branch_id =:branch_id";

        return template.query(sql, parameters, basicMembershipMapper);
    }

    public Optional<Membership> findById(String member_id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("member_id", member_id);
        String sql =
                "SELECT m.member_id, m.branch_id, m.name, m.age, m.status, m.profession, m.contactInfo_id, c.phoneNumber, c.emailAddress, c.country, c.city, c.street, c.building\n" +
                "FROM membership m\n" +
                "JOIN contactinfo c ON (m.contactInfo_id = c.id)\n" +
                "WHERE member_id = :member_id";
        Membership membership = template.queryForObject(sql, parameters, basicMembershipMapper);
        return membership != null ? Optional.of(membership) : Optional.empty();
    }

    public Membership save(Membership membership) {
        // Now Create Membership
        MapSqlParameterSource parametersMembership = new MapSqlParameterSource()
                .addValue("member_id", membership.getId())
                .addValue("branch_id", membership.getBranchId())
                .addValue("name", membership.getName())
                .addValue("age", membership.getAge())
                .addValue("status", membership.getStatus().toString())
                .addValue("profession", membership.getProfession().toString())
                .addValue("contactInfo_id", membership.getContactInfo().getId());
        String sqlCreateMembership =
                "INSERT INTO membership (member_id, name, age, status, profession, contactInfo_id, branch_id) " +
                "VALUES(:member_id, :name, :age, :status, :profession, :contactInfo_id, :branch_id)";
        String sqlUpdateMembership =
                        "UPDATE membership " +
                        "SET name=:name, age=:age, status=:status, profession=:profession, contactInfo_id=:contactInfo_id, branch_id=:branch_id " +
                        "WHERE member_id=:member_id";
        int count = template.update(sqlUpdateMembership, parametersMembership);
        if (count == 0) {
            template.update(sqlCreateMembership, parametersMembership);
        }

        return membership;
    }

    public void deleteAll() {
        MapSqlParameterSource parametersMembership = new MapSqlParameterSource();
        String sql = "DELETE FROM membership WHERE MEMBER_ID > '0'";
        template.update(sql, parametersMembership);
    }

    private final RowMapper<Membership> basicMembershipMapper = (resultSet, i) -> {
        ContactInfo contactInfo = new ContactInfo(
                resultSet.getInt("contactInfo_id"),
                resultSet.getString("phoneNumber"),
                resultSet.getString("emailAddress"),
                resultSet.getString("country"),
                resultSet.getString("city"),
                resultSet.getString("street"),
                resultSet.getString("building"));
        return new Membership(
                resultSet.getString("member_id"),
                resultSet.getString("branch_id"),
                resultSet.getString("name"),
                resultSet.getInt("age"),
                MemberStatus.valueOf(resultSet.getString("status")),
                MemberProfession.valueOf(resultSet.getString("profession")),
                contactInfo
        );
    };
}
