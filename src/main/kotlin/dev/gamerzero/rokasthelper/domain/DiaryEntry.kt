package dev.gamerzero.rokasthelper.domain

import io.github.thibaultmeyer.cuid.CUID
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.Instant

@Entity
class DiaryEntry(
    @ManyToOne
    var appUser: AppUser,

    val calories: Int,
    val proteins: Int,
    val fats: Int,
    val carbs: Int,

    val date: Instant = Instant.now(),
    @Id
    val id: String = CUID.randomCUID2().toString()
)