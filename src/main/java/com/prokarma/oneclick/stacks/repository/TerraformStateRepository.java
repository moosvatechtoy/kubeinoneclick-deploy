package com.prokarma.oneclick.stacks.repository;

import com.prokarma.oneclick.stacks.bo.TerraformState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerraformStateRepository extends MongoRepository<TerraformState, String> {
}