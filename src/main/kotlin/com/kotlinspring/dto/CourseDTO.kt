package com.kotlinspring.dto

import jakarta.validation.constraints.NotBlank

data class CourseDTO(
    val id: Int?,
    @get:NotBlank(message = "coutrseDTO.name must not be blank")
    val name: String,
    @get:NotBlank(message = "coutrseDTO.category must not be blank")
    val category: String
)
