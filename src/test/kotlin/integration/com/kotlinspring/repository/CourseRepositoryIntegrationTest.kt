package com.kotlinspring.repository

import com.kotlinspring.util.courseEntityList
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@DataJpaTest
@ActiveProfiles("test")
class CourseRepositoryIntegrationTest {

    @Autowired
    lateinit var courseRepository: CourseRepository

    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll()
        val courses = courseEntityList()
        courseRepository.saveAll(courses)
    }

    @Test
    fun retrieveAllCourses() {
        val courses = courseRepository.findByNameContaining("SpringBoot")

        assertEquals(2, courses.size)
    }

    @Test
    fun findCourseByName() {
        val courses = courseRepository.findCourseByName("SpringBoot")

        assertEquals(2, courses.size)
    }


}