package com.prokarma.oneclick.teams.repository

import com.prokarma.oneclick.teams.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String>
