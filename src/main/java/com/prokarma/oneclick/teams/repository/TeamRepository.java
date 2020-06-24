package com.prokarma.oneclick.teams.repository;

import com.prokarma.oneclick.teams.Team;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends MongoRepository<Team, String> {
}
