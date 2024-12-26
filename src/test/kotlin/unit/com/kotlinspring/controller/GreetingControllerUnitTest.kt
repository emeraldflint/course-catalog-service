package com.kotlinspring.controller

import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient
import com.kotlinspring.service.GreetingsService
import io.mockk.every
import org.junit.jupiter.api.Assertions

@WebMvcTest(controllers = [GreetingController::class])
@AutoConfigureWebTestClient
class GreetingControllerUnitTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var greetingsService: GreetingsService

    @Test
    fun testGreetingEndpoint() {

        // given
        val name = "Emerald"

        // when
        every { greetingsService.retrieveGreeting(name) } returns "$name, Hello from default profile"

        // then
        val result = webTestClient
            .get()
            .uri("/v1/greetings/{name}", name)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody(String::class.java)
            .returnResult()

        Assertions.assertEquals("$name, Hello from default profile", result.responseBody)
    }
}