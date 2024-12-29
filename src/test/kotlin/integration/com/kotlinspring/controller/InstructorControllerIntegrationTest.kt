package com.kotlinspring.controller

import com.kotlinspring.dto.InstructorDTO
import com.kotlinspring.repository.InstructorRepository
import com.kotlinspring.util.instructorsEntityList
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Testcontainers
class InstructorControllerIntegrationTest {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var instructorRepository: InstructorRepository

    companion object {
        @Container
        val postgresDB = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:13.3-alpine")).apply {
            withDatabaseName("testdb")
            withUsername("postgres")
            withPassword("secret")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresDB::getJdbcUrl)
            registry.add("spring.datasource.username", postgresDB::getUsername)
            registry.add("spring.datasource.password", postgresDB::getPassword)
        }
    }

    @BeforeEach
    fun setUp() {
        instructorRepository.deleteAll()

        val instructors = instructorsEntityList()
        instructorRepository.saveAll(instructors)
    }

    @Test
    @DisplayName("Add instructor")
    fun addInstructor() {
        val instructorDTO = InstructorDTO(
            null,
            "Emerald Flint"
        )


        val result = webTestClient
            .post()
            .uri("/v1/instructors")
            .bodyValue(instructorDTO)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody(InstructorDTO::class.java)
            .returnResult()
            .responseBody

        assertTrue { result!!.id != null }
    }

    @Test
    @DisplayName("Add instructor Validation")
    fun addInstructor_Validation() {
        val instructorDTO = InstructorDTO(
            null,
            ""
        )

        val result = webTestClient
            .post()
            .uri("/v1/instructors")
            .bodyValue(instructorDTO)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertEquals("InstructorDTO.name must not be blank", result)
    }
}