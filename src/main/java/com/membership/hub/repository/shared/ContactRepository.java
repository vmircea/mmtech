package com.membership.hub.repository.shared;

import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.model.shared.ContactInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ContactRepository {
    private NamedParameterJdbcTemplate template;

    @Autowired
    public void setDataSource(final DataSource dataSource){
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    public ContactInfo save(ContactInfo contactInfo) {
        // Create Contact Info
        String sqlUpdateContactInfo =
                        "UPDATE contactinfo " +
                        "SET phoneNumber = :phoneNumber, emailAddress = :emailAddress, country = :country, city = :city, street = :street, building = :building " +
                        "WHERE phoneNumber = :phoneNumber";
        String sqlCreateContactInfo =
                        "INSERT INTO contactinfo (id, phoneNumber, emailAddress, country, city, street, building)" +
                        "VALUES(:id, :phoneNumber, :emailAddress, :country, :city, :street, :building)";

        MapSqlParameterSource parametersContactInfo = new MapSqlParameterSource()
                .addValue("id", contactInfo.getId())
                .addValue("phoneNumber", contactInfo.getPhoneNumber())
                .addValue("emailAddress", contactInfo.getEmailAddress())
                .addValue("country", contactInfo.getCountry())
                .addValue("city", contactInfo.getCity())
                .addValue("street", contactInfo.getStreet())
                .addValue("building", contactInfo.getBuilding());
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        int count = template.update(sqlUpdateContactInfo, parametersContactInfo);
        if (count == 0) {
            template.update(sqlCreateContactInfo, parametersContactInfo, generatedKeyHolder);
            contactInfo.setId(Objects.requireNonNull(generatedKeyHolder.getKey()).intValue());
        }

        return this.findByPhoneNumber(contactInfo.getPhoneNumber()).get();
    }

    public Optional<ContactInfo> findByPhoneNumber(String phoneNumber) {
        String sqlFetch = "SELECT id, phoneNumber, emailAddress, country, city, street, building FROM contactinfo WHERE phoneNumber = :phoneNumber";
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("phoneNumber", phoneNumber);
        List<ContactInfo> contacts = template.query(sqlFetch, parameters, contactMapper);
        return getContactFromResultSet(contacts);
    }

    public void deleteAll() {
        MapSqlParameterSource parametersMembership = new MapSqlParameterSource();
        String sql = "DELETE FROM contactinfo WHERE id > 0";
        template.update(sql, parametersMembership);
    }

    private Optional<ContactInfo> getContactFromResultSet(List<ContactInfo> contacts) {
        if (contacts  != null && !contacts.isEmpty()) {
            return Optional.of(contacts.get(0));
        }
        else {
            return Optional.empty();
        }
    }

    private final RowMapper<ContactInfo> contactMapper = (resultSet, i) -> {
        return new ContactInfo(
            resultSet.getInt("id"),
                resultSet.getString("phoneNumber"),
                resultSet.getString("emailAddress"),
                resultSet.getString("country"),
                resultSet.getString("city"),
                resultSet.getString("street"),
                resultSet.getString("building")
        );
    };


}
