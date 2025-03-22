package dev.gamerzero.rokasthelper.repository

import dev.gamerzero.rokasthelper.domain.ChatMemory
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface ChatMemoryRepository : CrudRepository<ChatMemory, Long> {
    fun findFirstByUser_Id(userId: Long): Optional<ChatMemory>
}
