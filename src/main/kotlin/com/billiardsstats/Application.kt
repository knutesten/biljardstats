package com.billiardsstats

import com.billiardsstats.web.SparkService
import com.billiardsstats.web.auth.DiscoveryDocument
import com.billiardsstats.web.auth.OpenIdConnectAuth
import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine
import org.axonframework.eventsourcing.eventstore.jdbc.MySqlEventTableFactory
import org.axonframework.spring.jdbc.SpringDataSourceConnectionProvider
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager
import org.h2.tools.Server
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource


@SpringBootApplication
open class Application {
    @Bean
    open fun init(restServices: Array<SparkService>) = CommandLineRunner {
        val server = Server.createTcpServer().start()
        println("*****************")
        println("H2 tcp server started: " + server.url)
        println("*****************")

        restServices.forEach(SparkService::init)
    }

    @Bean
    @Qualifier("eventStoreDataSource")
    @ConfigurationProperties("datasource.event-store")
    open fun eventStoreDataSource() = DataSourceBuilder.create().build()!!

    @Bean
    @Primary
    @ConfigurationProperties("datasource.read")
    open fun readDataSource() = DataSourceBuilder.create().build()!!

    @Bean
    open fun eventStorageEngine(@Qualifier("eventStoreDataSource") datasource: DataSource,
                                transactionManager: PlatformTransactionManager): EventStorageEngine {
        val eventStorageEngine = JdbcEventStorageEngine(
                SpringDataSourceConnectionProvider(datasource),
                SpringTransactionManager(transactionManager))

        eventStorageEngine.createSchema(MySqlEventTableFactory.INSTANCE)

        return eventStorageEngine
    }

    @Bean
    open fun openIdConnectAuth(
            @Value("\${open-id-connect.redirect-uri}") redirectUri: String,
            @Value("\${open-id-connect.client-id}") clientId: String,
            @Value("\${open-id-connect.secret}") secret: String,
            @Value("\${open-id-connect.discovery-document-uri}") discoveryDocumentUri: String): OpenIdConnectAuth {
        return OpenIdConnectAuth(clientId, secret, redirectUri, DiscoveryDocument(discoveryDocumentUri))
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
