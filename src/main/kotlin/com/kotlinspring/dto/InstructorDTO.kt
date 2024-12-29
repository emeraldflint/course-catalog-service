package com.kotlinspring.dto

import jakarta.validation.constraints.NotBlank

data class InstructorDTO(
    val id: Int? = null,
    @get:NotBlank(message = "InstructorDTO.name must not be blank")
    var name: String
)
