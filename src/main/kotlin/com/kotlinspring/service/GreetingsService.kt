package com.kotlinspring.service

import org.springframework.stereotype.Service

@Service
class GreetingsService {
    fun retrieveGreeting(name: String): String = "Hello $name"
}