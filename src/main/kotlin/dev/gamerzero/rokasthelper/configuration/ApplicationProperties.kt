package dev.gamerzero.rokasthelper.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application")
data class ApplicationProperties(
    val telegramBotToken: String,
    val openAiKey: String
)
