package com.membership.hub.repository;

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

        template.update(sqlSave, parameters);
        return transaction;
    }

    public List<PaymentModel> findAllById(String id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("branchId", id);
        String sqlFindAll = "SELECT sender_id, receiver_id, amount, date, description FROM payments WHERE sender_id=:branchId;";

        return template.query(sqlFindAll, parameters, paymentMapper);
    }

    private final RowMapper<PaymentModel> paymentMapper = (resultSet, i) -> {
        return new PaymentModel(
                resultSet.getString("sender_id"),
                resultSet.getString("receiver_id"),
                resultSet.getDouble("amount"),
                LocalDate.parse(resultSet.getString("date")),
                resultSet.getString("description"));
    };
}
