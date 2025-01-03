package com.kotlinspring.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table


@Entity
@Table(name = "Courses")
data class Course(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int?,
    var name: String,
    var category: String,
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "INSTRUCTOR_ID", nullable = false)
    var instructor: Instructor?
) {
    // No-args constructor
    constructor() : this(null, "", "", Instructor())

    override fun toString(): String {
        return "Course(id=$id, name='$name', category='$category', instructor=${instructor?.id})"
    }


}
