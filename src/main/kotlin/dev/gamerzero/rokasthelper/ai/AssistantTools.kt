package dev.gamerzero.rokasthelper.ai

import dev.gamerzero.rokasthelper.domain.DiaryEntry
import dev.gamerzero.rokasthelper.domain.DietType
import dev.gamerzero.rokasthelper.repository.AppUserRepository
import dev.gamerzero.rokasthelper.repository.DiaryEntryRepository
import dev.gamerzero.rokasthelper.util.reference
import dev.gamerzero.rokasthelper.util.toNullable
import dev.langchain4j.agent.tool.Tool
import dev.langchain4j.agent.tool.ToolMemoryId
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.Period

data class NutritionReport(
    val dietType: DietType,
    val consumedCalories: Int,
    val consumedProteins: Int,
    val consumedFats: Int,
    val consumedCarbs: Int,
)

@Component
class AssistantTools(
    private val appUserRepository: AppUserRepository,
    private val diaryEntryRepository: DiaryEntryRepository
) {
    @Tool
    @Transactional
    fun setDietType(@ToolMemoryId userId: Long, dietType: DietType) {
        val user = appUserRepository.reference(userId)
            ?: throw Exception("User with id $userId not found")
        user.dietType = dietType
        appUserRepository.save(user)
    }

    @Tool
    @Transactional
    fun addExcludedIngredients(@ToolMemoryId userId: Long, ingredients: List<String>) {
        val user = appUserRepository.reference(userId)
            ?: throw Exception("User with id $userId not found")
        val decodedJson = Json.decodeFromString<List<String>>(user.excludedIngredients)
        user.excludedIngredients = Json.encodeToString(decodedJson + ingredients)
        appUserRepository.save(user)
    }

    @Tool
    fun addDiaryEntry(
        @ToolMemoryId userId: Long, calories: Int,
        proteins: Int,
        fats: Int,
        carbs: Int
    ) {
        diaryEntryRepository.save(
            DiaryEntry(
                calories = calories,
                proteins = proteins,
                fats = fats,
                carbs = carbs,
                appUser = appUserRepository.getReferenceById(userId)
            )
        )
    }

    @Tool
    fun getWeeklyNutritionReport(@ToolMemoryId userId: Long): NutritionReport {
        val dietType = appUserRepository.findById(userId).toNullable()?.dietType
            ?: throw Exception("User with id $userId not found")

        val weekBefore = Instant.now() - Period.ofDays(7)
        val entries = diaryEntryRepository.findDiaryEntryByDateAfter(weekBefore)

        return NutritionReport(
            dietType = dietType,
            consumedCalories = entries.sumOf { it.calories },
            consumedProteins = entries.sumOf { it.proteins },
            consumedFats = entries.sumOf { it.fats },
            consumedCarbs = entries.sumOf { it.carbs },
        )
    }
}