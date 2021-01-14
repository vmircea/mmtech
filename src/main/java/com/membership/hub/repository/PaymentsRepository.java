package com.membership.hub.repository;

import com.membership.hub.model.membership.MembershipFeeModel;
import com.membership.hub.model.shared.PaymentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

@Repository
public class PaymentsRepository {
    private NamedParameterJdbcTemplate template;

    @Autowired
    public void setDataSource(final DataSource dataSource){
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    public PaymentModel save(PaymentModel transaction) {
        String sqlSave =
                "INSERT INTO payments (sender_id, receiver_id, amount, date, description) " +
                "VALUES (:sender_id, :receiver_id, :amount, :date, :description)";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("sender_id", transaction.getSenderId())
                .addValue("receiver_id", transaction.getReceiverId())
                .addValue("amount", transaction.getAmount())
                .addValue("date", transaction.getDate())
                .addValue("description", transaction.getDescription());

        int count = template.update(sqlSave, parameters);
        if (count == 1) {
            return transaction;
        }
        return null;
    }

    public List<PaymentModel> findAllById(String id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("branchId", id);
        String sqlFindAll = "SELECT sender_id, receiver_id, amount, date, description FROM payments WHERE sender_id=:branchId;";

        return template.query(sqlFindAll, parameters, paymentMapper);
    }

    public List<MembershipFeeModel> findAllMembershipFeesById(String id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("memberId", id);

        String sqlFindAllFees = "SELECT date, amount FROM payments WHERE sender_id=:memberId;";

        return template.query(sqlFindAllFees, parameters, feeMapper);
    }

    private final RowMapper<PaymentModel> paymentMapper = (resultSet, i) -> new PaymentModel(
            resultSet.getString("sender_id"),
            resultSet.getString("receiver_id"),
            resultSet.getDouble("amount"),
            LocalDate.parse(resultSet.getString("date")),
            resultSet.getString("description"));

    private final RowMapper<MembershipFeeModel> feeMapper = (resultSet, i) -> {
        LocalDate date = LocalDate.parse(resultSet.getString("date"));
        Double amount = resultSet.getDouble("amount");
        return new MembershipFeeModel(date, amount);
    };


}
