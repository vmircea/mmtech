package com.membership.hub.repository;

import com.membership.hub.model.Membership;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MembershipRepositoryInterface {
    List<Membership> findAll();
    Optional<Membership> findById(String id);
    Membership save(Membership membership) throws IOException;
    void deleteAll();
}
