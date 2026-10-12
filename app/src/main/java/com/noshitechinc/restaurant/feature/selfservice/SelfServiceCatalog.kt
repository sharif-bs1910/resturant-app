package com.noshitechinc.restaurant.feature.selfservice

import androidx.annotation.StringRes
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.feature.checkout.MenuCatalog
import com.noshitechinc.restaurant.feature.checkout.MenuCategory
import com.noshitechinc.restaurant.feature.checkout.MenuItem

enum class SelfServiceMeal(@param:StringRes val labelRes: Int, val count: Int) {
    Dinner(R.string.self_meal_dinner, 38),
    Lunch(R.string.self_meal_lunch, 24),
    Brunch(R.string.self_meal_brunch, 18),
    Drinks(R.string.self_meal_drinks, 32),
}

enum class SelfServiceOrderType(@param:StringRes val labelRes: Int) {
    DineIn(R.string.self_order_dine_in),
    Takeout(R.string.self_order_takeout),
}

enum class SelfServiceCategory(@param:StringRes val labelRes: Int, val count: Int) {
    Starters(R.string.self_cat_starters, 8),
    Pizzas(R.string.self_cat_pizzas, 14),
    Pastas(R.string.self_cat_pastas, 8),
    Mains(R.string.self_cat_mains, 4),
    Sides(R.string.self_cat_sides, 6),
    Desserts(R.string.self_cat_desserts, 4),
    Drinks(R.string.self_cat_drinks, 12),
}

/** Maps Figma self-service sections onto the shared demo catalog. */
fun SelfServiceCategory.matches(item: MenuItem): Boolean = when (this) {
    SelfServiceCategory.Starters -> item.id in setOf("gyoza", "edamame")
    SelfServiceCategory.Pizzas -> item.category == MenuCategory.Ramen
    SelfServiceCategory.Pastas -> item.id == "gyudon"
    SelfServiceCategory.Mains -> item.id == "karaage"
    SelfServiceCategory.Sides -> item.category == MenuCategory.Sides && item.id !in setOf("gyoza", "edamame")
    SelfServiceCategory.Desserts -> item.id == "matcha"
    SelfServiceCategory.Drinks -> item.category == MenuCategory.Drinks
}

fun filteredSelfServiceMenu(meal: SelfServiceMeal, category: SelfServiceCategory, query: String): List<MenuItem> {
    val q = query.trim()
    return MenuCatalog.filter { item ->
        val mealMatches = when (meal) {
            SelfServiceMeal.Drinks -> item.category == MenuCategory.Drinks
            else -> true
        }
        val categoryMatches = if (meal == SelfServiceMeal.Drinks) {
            item.category == MenuCategory.Drinks
        } else {
            category.matches(item)
        }
        val textMatches = q.isEmpty() || item.name.contains(q, ignoreCase = true)
        mealMatches && categoryMatches && textMatches
    }.ifEmpty {
        // Keep the grid usable when a section has no mapped demo SKUs.
        MenuCatalog.filter { item ->
            val mealMatches = when (meal) {
                SelfServiceMeal.Drinks -> item.category == MenuCategory.Drinks
                else -> item.category != MenuCategory.Drinks
            }
            val textMatches = q.isEmpty() || item.name.contains(q, ignoreCase = true)
            mealMatches && textMatches
        }
    }
}
