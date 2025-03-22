package dev.gamerzero.rokasthelper.ai

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import org.springframework.context.annotation.Bean

@Component
class ChatMemoryFactory(private val persistentChatMemoryStore: PersistentChatMemoryStore) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun chatMemoryProvider(): ChatMemoryProvider = ChatMemoryProvider {
        logger.debug("Initializing chat memory")

        MessageWindowChatMemory
            .builder()
            .id(it)
            .maxMessages(5)
            .chatMemoryStore(persistentChatMemoryStore)
            .build()
    }
}
