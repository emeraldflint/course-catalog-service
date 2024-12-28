package com.kotlinspring.util

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course

fun courseEntityList() = listOf(
    Course(null, "Build Restful APIs using SpringBoot and Kotlin", "Development"),
    Course(null, "Build Reactive Microservices using SpringBoot WebFlux/SpringBoot", "Development"),
    Course(null, "Wiremock for Java Developers", "Development")
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