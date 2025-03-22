package dev.gamerzero.rokasthelper.repository

import dev.gamerzero.rokasthelper.domain.DiaryEntry
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface DiaryEntryRepository : JpaRepository<DiaryEntry, Long> {
    fun findDiaryEntryByDateAfter(after: Instant): List<DiaryEntry>
}