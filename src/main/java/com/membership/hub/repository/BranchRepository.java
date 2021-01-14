package com.membership.hub.repository;

import com.membership.hub.model.branch.BranchModel;
import com.membership.hub.model.shared.ContactInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class BranchRepository {
    private NamedParameterJdbcTemplate template;

    @Autowired
    public void setDataSource(final DataSource dataSource){
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    public BranchModel save(BranchModel newBranch) {
        String sqlUpdateInfo =
                "UPDATE branches " +
                "SET branch_name= :branch_name, admin_id= :admin_id, contactInfo_id= :contactInfo_id " +
                "WHERE branch_id=:branch_id";
        String sqlCreate =
                        "INSERT INTO branches (branch_id, branch_name, admin_id, contactInfo_id, totalAmount) " +
                        "VALUES (:branch_id, :branch_name, :admin_id, :contactInfo_id, :totalAmount)";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("branch_id", newBranch.getBranchId())
                .addValue("branch_name", newBranch.getBranchName())
                .addValue("admin_id", newBranch.getAdminId())
                .addValue("contactInfo_id", newBranch.getContactInfo().getId())
                .addValue("totalAmount", 0.0);

        int count = template.update(sqlUpdateInfo, parameters);
        if (count == 0) {
            template.update(sqlCreate, parameters);
            newBranch.setBranchAmount(0.0);
        }

        return newBranch;
    }

    public boolean updateAmount(String branchId, Double paymentAmount) {
        String sqlUpdateAmount =
                "UPDATE branches " +
                "SET totalAmount = totalAmount + :paymentAmount " +
                "WHERE branch_id=:branch_id";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("branch_id", branchId)
                .addValue("paymentAmount", paymentAmount);

        int count = template.update(sqlUpdateAmount, parameters);
        return count == 1;
    }

    public Optional<BranchModel> findById(String id) {
        String sqlFind =
                        "SELECT b.branch_id, b.branch_name, b.admin_id, b.contactInfo_id, b.totalAmount, c.phoneNumber, c.emailAddress, c.country, c.city, c.street, c.building " +
                        "FROM branches b " +
                        "JOIN contactinfo c ON (b.contactInfo_id = c.id) " +
                        "WHERE branch_id = :branch_id;";
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("branch_id", id);

        List<BranchModel> fetchedBranch = template.query(sqlFind, parameters, branchMapper);
        return getBranchFromResultSet(fetchedBranch);
    }

    public List<BranchModel> findAll() {
        String sqlFindAll =
                "SELECT b.branch_id, b.branch_name, b.admin_id, b.contactInfo_id, b.totalAmount, c.phoneNumber, c.emailAddress, c.country, c.city, c.street, c.building " +
                "FROM branches b " +
                "JOIN contactinfo c ON (b.contactInfo_id = c.id) ";

        return template.query(sqlFindAll, branchMapper);
    }

    public Optional<BranchModel> deleteBranch(String id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("branch_id", id);
        String sqlDelete = "DELETE FROM branches WHERE branch_id = :branch_id;";

        Optional<BranchModel> existingBranch = this.findById(id);
        if(existingBranch.isPresent()) {
            template.update(sqlDelete, parameters);
            return existingBranch;
        }
        return Optional.empty();
    }

    private Optional<BranchModel> getBranchFromResultSet(List<BranchModel> branches) {
        if (branches  != null && !branches.isEmpty()) {
            return Optional.of(branches.get(0));
        }
        else {
            return Optional.empty();
        }
    }

    private final RowMapper<BranchModel> branchMapper = (resultSet, i) -> {
        ContactInfo contactInfo = new ContactInfo(
                resultSet.getInt("contactInfo_id"),
                resultSet.getString("phoneNumber"),
                resultSet.getString("emailAddress"),
                resultSet.getString("country"),
                resultSet.getString("city"),
                resultSet.getString("street"),
                resultSet.getString("building"));
        BranchModel branchFromDB = new BranchModel(
                resultSet.getString("branch_id"),
                resultSet.getString("admin_id"),
                resultSet.getString("branch_name"),
                contactInfo,
                resultSet.getDouble("totalAmount")
        );
        return branchFromDB;
    };

}
