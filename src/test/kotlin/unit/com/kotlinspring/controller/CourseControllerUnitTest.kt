package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.service.CourseService
import com.kotlinspring.util.courseDTO
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient

@WebMvcTest(CourseController::class)
@AutoConfigureWebTestClient
class CourseControllerUnitTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var courseServiceMockk: CourseService

    @Test
    fun addCourse() {
        val courseDTO = CourseDTO(null, "Build Restful APIs using Spring and Kotlin", "Development")

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
    fun retrieveAllCourses() {
        every { courseServiceMockk.getAllCourses() } returns listOf(
            courseDTO(id = 1, name = "Build Restful APIs using Spring and Kotlin", category = "Development"),
            courseDTO(id = 2, name = "Build Reactive Microservices using Spring WebFlux/SpringBoot", category = "Development"),
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

}