package com.membership.hub.repository;

import com.membership.hub.model.MembershipFeeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class FeesRepository {
    private NamedParameterJdbcTemplate template;

    @Autowired
    public void setDataSource(final DataSource dataSource){
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void save(MembershipFeeModel membershipFeeModel, String id) {
        String sqlCreateFee = "INSERT INTO fees (paidInDate, paidInAmount, member_id) VALUES (:paidInDate, :paidInAmount, :member_id)";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("paidInDate", membershipFeeModel.getPaidInDate())
                .addValue("paidInAmount", membershipFeeModel.getPaidInAmount())
                .addValue("member_id", id);

        template.update(sqlCreateFee, parameters);
    }

    public Optional<List<MembershipFeeModel>> findById(String id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("member_id", id);
        String sqlFetch =
                "SELECT paidInDate, paidInAmount, member_id\n" +
                "FROM fees\n" +
                "WHERE member_id = :member_id";
        List<MembershipFeeModel> fees = template.query(sqlFetch, parameters, feesMapper);
        return fees != null ? Optional.of(fees) : Optional.empty();
    }

    private final RowMapper<MembershipFeeModel> feesMapper = (resultSet, i) -> {
        String date = resultSet.getString("paidInDate");
        LocalDate paidInDate = LocalDate.parse(date);
        Double paidInAmount = resultSet.getDouble("paidInAmount");

        return new MembershipFeeModel(paidInDate, paidInAmount);
    };
}
