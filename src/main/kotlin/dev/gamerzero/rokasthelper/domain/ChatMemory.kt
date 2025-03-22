package dev.gamerzero.rokasthelper.domain

import io.github.thibaultmeyer.cuid.CUID
import jakarta.persistence.*

@Entity
class ChatMemory(
    @OneToOne val user: AppUser,

    @Column(columnDefinition = "text")
    var messages: String = "[]",

    @Id
    val id: String = CUID.randomCUID2().toString()
)
