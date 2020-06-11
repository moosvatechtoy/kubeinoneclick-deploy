package com.prokarma.oneclick;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.prokarma.oneclick.test.MongoContainer;

@SpringBootTest
@Testcontainers
class OneclickIT {

	@Container
	private static MongoContainer mongoContainer = new MongoContainer();

	@Autowired
	private OneclickApplication oneclick;

	@Test
	void contextLoads() {
		assertNotNull(oneclick);
	}

}
