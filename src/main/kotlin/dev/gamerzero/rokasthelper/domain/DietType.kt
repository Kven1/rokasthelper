package dev.gamerzero.rokasthelper.domain

enum class DietType(
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbs: Int
) {
    MUSCLE_GAIN(
        calories = 19600,
        protein = 980,
        fat = 560,
        carbs = 2660
    ),
    WEIGHT_LOSS(
        calories = 14000,
        protein = 980,
        fat = 385,
        carbs = 1640
    ),
    MAINTENANCE(
        calories = 17500,
        protein = 880,
        fat = 480,
        carbs = 2300
    )
}