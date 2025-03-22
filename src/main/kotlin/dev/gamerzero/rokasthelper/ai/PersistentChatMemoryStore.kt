package dev.gamerzero.rokasthelper.ai

import dev.gamerzero.rokasthelper.repository.ChatMemoryRepository
import dev.gamerzero.rokasthelper.util.toNullable
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.ChatMessageDeserializer
import dev.langchain4j.data.message.ChatMessageSerializer
import dev.langchain4j.store.memory.chat.ChatMemoryStore
import org.springframework.stereotype.Component

@Component
class PersistentChatMemoryStore(
    private val chatMemoryRepository: ChatMemoryRepository,
) : ChatMemoryStore {

    override fun getMessages(memoryId: Any?): MutableList<ChatMessage> {
        val key = parseKey(memoryId)

        val memory = chatMemoryRepository.findFirstByUser_Id(key).toNullable()
            ?: throw Exception("Chat memory for key $memoryId not found")

        return ChatMessageDeserializer.messagesFromJson(memory.messages)
    }

    override fun updateMessages(memoryId: Any?, messages: MutableList<ChatMessage>?) {
        val key = parseKey(memoryId)

        val memory = chatMemoryRepository.findFirstByUser_Id(key).toNullable()
            ?: throw Exception("Chat memory for key $memoryId not found")

        memory.messages = ChatMessageSerializer.messagesToJson(messages)
        chatMemoryRepository.save(memory)
    }

    override fun deleteMessages(memoryId: Any?) {
        val key = parseKey(memoryId)

        chatMemoryRepository.deleteById(key)
    }

    private fun parseKey(memoryId: Any?): Long {
        if (memoryId !is Long) throw IllegalArgumentException("memoryId must be a Long, but was ${memoryId?.javaClass?.name}: $memoryId")
        return memoryId
    }
}
