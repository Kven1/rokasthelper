package dev.gamerzero.rokasthelper.repository

import dev.gamerzero.rokasthelper.domain.AppUser
import org.springframework.data.jpa.repository.JpaRepository


interface AppUserRepository : JpaRepository<AppUser, Long>
