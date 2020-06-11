package com.prokarma.oneclick.teams.repository

import com.prokarma.oneclick.teams.Team
import com.prokarma.oneclick.teams.User
import com.prokarma.oneclick.test.MongoContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.annotation.DirtiesContext
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataMongoTest
@Testcontainers
@DirtiesContext
class UserRepositoryIT {

    @Autowired
    lateinit var userRepository: UserRepository

    companion object {
        @Container
        val mongoContainer = com.prokarma.oneclick.test.MongoContainer()
    }

    @Test
    fun user_shouldBeSaved() {
        // given
        val sam = User("Samantha Carter", Team("SG-1"))

        // when
        userRepository.save(sam)

        // then
        val result = userRepository.findById("Samantha Carter")
        assertThat(result)
            .isNotNull
            .isPresent
            .hasValueSatisfying { assertThat(it.username).isEqualTo("Samantha Carter") }
    }

}
