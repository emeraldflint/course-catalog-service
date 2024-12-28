package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import com.kotlinspring.util.courseEntityList
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class CourseControllerIntegrationTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll()

        val courses = courseEntityList()
        courseRepository.saveAll(courses)
    }

    @Test
    @DisplayName("Add course")
    fun addCourse() {
        val courseDTO = CourseDTO(
            null,
            "Build Restful APIs using Kotlin",
            "Emerald Flint"
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
        val course = Course(null, "Build Restful APIs using Spring and Kotlin", "Development")
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
        val course = Course(null, "Build Restful APIs using Spring and Kotlin", "Development")
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