package com.prokarma.oneclick.stacks.repository;

import com.prokarma.oneclick.stacks.bo.Step;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for steps
 */
@Repository
public interface StepRepository extends MongoRepository<Step, String> {

    void deleteByJobId(String jobId);

}
