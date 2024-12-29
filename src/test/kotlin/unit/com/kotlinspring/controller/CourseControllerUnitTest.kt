package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.service.CourseService
import com.kotlinspring.util.courseDTO
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals

@WebMvcTest(CourseController::class)
@AutoConfigureWebTestClient
class CourseControllerUnitTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var courseServiceMockk: CourseService

    @Test
    fun addCourse() {
        val courseDTO = CourseDTO(null, "Build Restful APIs using Spring and Kotlin", "Development", 1)

        every { courseServiceMockk.addCourse(any()) } returns courseDTO(id = 1)

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
    fun addCourse_validation() {
        val courseDTO = CourseDTO(null, "", "", 1)

        every { courseServiceMockk.addCourse(any()) } returns courseDTO(id = 1)

        val response = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertEquals("courseDTO.category must not be blank, courseDTO.name must not be blank", response)
    }

    @Test
    fun addCourse_runtimeException() {
        val courseDTO = CourseDTO(null, "Build Restful APIs using Spring and Kotlin", "Development", 1)


        val errorMessage = "Unexpected error occurred"

        every { courseServiceMockk.addCourse(any()) } throws RuntimeException(errorMessage)

        val response = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus()
            .is5xxServerError
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertEquals(errorMessage, response)
    }

    @Test
    fun retrieveAllCourses() {
        every { courseServiceMockk.getAllCourses(any()) } returns listOf(
            courseDTO(id = 1, name = "Build Restful APIs using Spring and Kotlin", category = "Development"),
            courseDTO(
                id = 2,
                name = "Build Reactive Microservices using Spring WebFlux/SpringBoot",
                category = "Development"
            ),
            courseDTO(id = 3, name = "Wiremock for Java Developers", category = "Development")
        )

        val result = webTestClient
            .get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertTrue { result!!.size == 3 }
    }

    @Test
    fun updateCourse() {
        val updatedCourseEntity = CourseDTO(
            null,
            "Apache Kafka for Developers using Spring Boot1", "Development"
        )

        every { courseServiceMockk.updateCourse(any(), any()) } returns CourseDTO(
            100,
            "Apache Kafka for Developers using Spring Boot1", "Development"
        )


        val updatedCourseDTO = webTestClient
            .put()
            .uri("/v1/courses/{courseId}", 100)
            .bodyValue(updatedCourseEntity)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals("Apache Kafka for Developers using Spring Boot1", updatedCourseDTO?.name)
    }

    @Test
    fun deleteCourse() {

        every { courseServiceMockk.deleteCourse(any()) } just runs

        webTestClient
            .delete()
            .uri("/v1/courses/{courseId}", 100)
            .exchange()
            .expectStatus().isNoContent

        verify(exactly = 1) { courseServiceMockk.deleteCourse(any()) }
    }

}