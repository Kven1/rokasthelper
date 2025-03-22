package dev.gamerzero.rokasthelper.domain

import jakarta.persistence.*

@Entity
class AppUser(
    @Id val id: Long,
    val username: String,

    var dietType: DietType? = null,

    var excludedIngredients: String = "[]",

    @OneToMany(fetch = FetchType.LAZY)
    val diaryEntries: List<DiaryEntry> = emptyList(),

    @OneToOne var chatMemory: ChatMemory? = null
)

