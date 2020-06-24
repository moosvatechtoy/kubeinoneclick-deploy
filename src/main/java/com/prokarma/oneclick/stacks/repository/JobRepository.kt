package com.prokarma.oneclick.stacks.repository

import com.prokarma.oneclick.stacks.bo.Job
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Repository for jobs
 */
@Repository
interface JobRepository : MongoRepository<com.prokarma.oneclick.stacks.bo.Job, String> {

    fun findAllByStackIdOrderByStartDateTimeDesc(stackId: String): List<com.prokarma.oneclick.stacks.bo.Job>

    fun deleteByStackId(stackId: String)

}
