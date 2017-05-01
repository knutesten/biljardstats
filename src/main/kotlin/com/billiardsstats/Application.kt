package com.billiardsstats

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class Application {
    @Bean
    open fun init() = CommandLineRunner {
        println("server started")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}

