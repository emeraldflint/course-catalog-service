package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import com.kotlinspring.repository.InstructorRepository
import com.kotlinspring.util.courseEntityList
import com.kotlinspring.util.instructorEntity
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
import org.springframework.web.util.UriComponentsBuilder
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
class CourseControllerIntegrationTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

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

        courseRepository.deleteAll()
        instructorRepository.deleteAll()

        val instructor = instructorEntity()
        instructorRepository.save(instructor)

        val courses = courseEntityList(instructor)
        courseRepository.saveAll(courses)
    }

    @Test
    @DisplayName("Add course")
    fun addCourse() {
        val instructor = instructorRepository.findAll().first()

        val courseDTO = CourseDTO(
            null,
            "Build Restful APIs using Kotlin",
            "Emerald Flint",
            instructor.id
        )


        val result = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertTrue { result!!.id != null }
    }

    @Test
    @DisplayName("Retrieve all courses")
    fun retrieveAllCourses() {
        val coursesDTOs = webTestClient
            .get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals(3, coursesDTOs!!.size)
    }

    @Test
    @DisplayName("Update course")
    fun updateCourse() {
        //existing course
        val instructor = instructorRepository.findAll().first()

        val course = Course(null, "Build Restful APIs using Spring and Kotlin", "Development", instructor)
        courseRepository.save(course)

        //Updated CourserDto
        val updatedCourseDTO = CourseDTO(null, "Build Restful APIs using Spring and Kotlin1", "Development")
        val updatedCourse = webTestClient
            .put()
            .uri("/v1/courses/{course_id}", course.id)
            .bodyValue(updatedCourseDTO)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals("Build Restful APIs using Spring and Kotlin1", updatedCourse!!.name)
    }

    @Test
    @DisplayName("Delete course")
    fun deleteCourse() {
        //existing course
        val instructor = instructorRepository.findAll().first()
        val course = Course(null, "Build Restful APIs using Spring and Kotlin", "Development", instructor)
        courseRepository.save(course)

        //Delete course
        webTestClient
            .delete()
            .uri("/v1/courses/{course_id}", course.id)
            .exchange()
            .expectStatus()
            .isNoContent
    }

    @Test
    fun retrieveAllCourses_ByName() {
        val uri = UriComponentsBuilder.fromUriString("/v1/courses")
            .queryParam("course_name", "SpringBoot")
            .toUriString()

        val courseDTO = webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals(2, courseDTO!!.size)
    }

}