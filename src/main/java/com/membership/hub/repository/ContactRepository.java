package com.membership.hub.repository;

import com.membership.hub.model.ContactInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Objects;

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
                        "WHERE id = :id";
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
        return contactInfo;
    }

    public void deleteAll() {
        MapSqlParameterSource parametersMembership = new MapSqlParameterSource();
        String sql = "DELETE FROM contactinfo WHERE id > 0";
        template.update(sql, parametersMembership);
    }


}
