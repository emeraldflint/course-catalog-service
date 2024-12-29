package com.kotlinspring.util

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.dto.InstructorDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.entity.Instructor

fun courseEntityList() = listOf(
    Course(null, "Build Restful APIs using SpringBoot and Kotlin", "Development", Instructor()),
    Course(null, "Build Reactive Microservices using SpringBoot WebFlux/SpringBoot", "Development", Instructor()),
    Course(null, "Wiremock for Java Developers", "Development", Instructor())
)

fun courseDTO(
    id: Int? = null,
    name: String = "Build Restful APIs using Spring Boot and Kotlin",
    category: String = "Development"
) = CourseDTO(
    id,
    name,
    category
)

fun instructorsEntityList() = listOf(
    Instructor(null, "Eugene Black", listOf()),
    Instructor(null, "Emerald Flint", listOf()),
    Instructor(null, "Alex Smith", listOf())
)

fun instructorDTO(
    id: Int? = null,
    name: String = "Eugene Black"
) = InstructorDTO(
    id,
    name
)