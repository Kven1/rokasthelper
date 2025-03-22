package dev.gamerzero.rokasthelper.service

import dev.gamerzero.rokasthelper.domain.AppUser
import dev.gamerzero.rokasthelper.domain.ChatMemory
import dev.gamerzero.rokasthelper.repository.AppUserRepository
import dev.gamerzero.rokasthelper.repository.ChatMemoryRepository
import dev.gamerzero.rokasthelper.util.orElse
import dev.gamerzero.rokasthelper.util.toNullable
import org.springframework.stereotype.Service

@Service
class AppUserService(
    private val appUserRepository: AppUserRepository,
    private val chatMemoryRepository: ChatMemoryRepository
) {

    fun findOrCreateUser(userId: Long, userName: String): AppUser =
        appUserRepository.findById(userId).toNullable()
            .orElse {
                val user = AppUser(userId, userName)
                appUserRepository.save(user)
                user.chatMemory = chatMemoryRepository.save(ChatMemory(user))
                appUserRepository.save(user)
            }
}
