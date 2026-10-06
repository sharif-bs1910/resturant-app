package com.noshitechinc.restaurant.feature.checkout

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.noshitechinc.restaurant.R

enum class MenuCategory(@param:StringRes val labelRes: Int) {
    All(R.string.checkout_category_all),
    Ramen(R.string.checkout_category_ramen),
    Donburi(R.string.checkout_category_donburi),
    Sides(R.string.checkout_category_sides),
    Drinks(R.string.checkout_category_drinks),
}

enum class OrderChannel(@param:StringRes val labelRes: Int) {
    InStore(R.string.checkout_in_store),
    Phone(R.string.checkout_phone),
}

enum class Fulfillment(@param:StringRes val labelRes: Int) {
    Pickup(R.string.checkout_pickup),
    Delivery(R.string.checkout_delivery),
}

enum class Settlement {
    Cash,
    Card,
}

enum class OpenOrderStatus(@param:StringRes val labelRes: Int, @param:StringRes val boardLabelRes: Int) {
    New(R.string.checkout_status_new, R.string.checkout_board_new),
    Cooking(R.string.checkout_status_cooking, R.string.checkout_board_cooking),
    Ready(R.string.checkout_status_ready, R.string.checkout_board_ready),
    Completed(R.string.checkout_status_completed, R.string.checkout_board_completed),
}

data class MenuItem(val id: String, val name: String, val category: MenuCategory, val priceCents: Int)

data class ModifierOption(val id: String, @param:StringRes val labelRes: Int, val priceCents: Int = 0)

data class ModifierGroup(val id: String, val titleRes: Int, val single: Boolean, val options: List<ModifierOption>)

data class CartLine(val id: String, val name: String, val unitCents: Int, val quantity: Int)

data class OpenOrder(
    val number: String,
    val guest: String,
    val status: OpenOrderStatus,
    val summary: String,
    val meta: String,
    val totalCents: Int,
)

data class ModifierDraft(val item: MenuItem, val selected: Set<String>, val quantity: Int, val note: String)

private const val TaxNumerator = 875
private const val TaxDenominator = 10_000
private const val RoundHalf = 5_000
const val GiftCardBalanceCents = 2_500

val MenuCatalog = listOf(
    MenuItem("spicy-tonkotsu", "Spicy Tonkotsu", MenuCategory.Ramen, 1_450),
    MenuItem("shoyu", "Shoyu Ramen", MenuCategory.Ramen, 1_300),
    MenuItem("miso", "Miso Ramen", MenuCategory.Ramen, 1_350),
    MenuItem("gyoza", "Pork Gyoza", MenuCategory.Sides, 700),
    MenuItem("edamame", "Edamame", MenuCategory.Sides, 550),
    MenuItem("matcha", "Matcha Lemonade", MenuCategory.Drinks, 500),
    MenuItem("gyudon", "Gyudon", MenuCategory.Donburi, 1_350),
    MenuItem("karaage", "Chicken Karaage", MenuCategory.Sides, 850),
    MenuItem("hojicha", "Iced Hojicha", MenuCategory.Drinks, 500),
)

val SampleCart = listOf(
    CartLine("line-spicy", "Spicy Tonkotsu", 1_450, 2),
    CartLine("line-gyoza", "Pork Gyoza", 700, 1),
    CartLine("line-matcha", "Matcha Lemonade", 500, 1),
)

val PhoneCart = listOf(
    CartLine("line-spicy", "Spicy Tonkotsu", 1_450, 2),
    CartLine("line-gyoza", "Pork Gyoza", 700, 1),
)

enum class QueueFilter(@param:StringRes val labelRes: Int, val status: OpenOrderStatus?) {
    All(R.string.checkout_filter_all, null),
    New(R.string.checkout_filter_new, OpenOrderStatus.New),
    Cooking(R.string.checkout_filter_cooking, OpenOrderStatus.Cooking),
    Ready(R.string.checkout_filter_ready, OpenOrderStatus.Ready),
}

data class QueueLine(val name: String, val quantity: Int, val unitCents: Int)

data class QueueOrder(
    val number: String,
    val guest: String,
    val status: OpenOrderStatus,
    val subtitle: String,
    val meta: String,
    val channel: OrderChannel,
    val fulfillment: Fulfillment,
    val lines: List<QueueLine>,
) {
    val subtotalCents: Int = lines.sumOf { it.unitCents * it.quantity }
    val orderTaxCents: Int get() = taxCents(subtotalCents)
    val totalCents: Int get() = subtotalCents + orderTaxCents

    fun toCart(): List<CartLine> = lines.mapIndexed { index, line ->
        CartLine("queue-$number-$index", line.name, line.unitCents, line.quantity)
    }
}

val QueueOrders = listOf(
    QueueOrder(
        "1042",
        "Maya R.",
        OpenOrderStatus.New,
        "New · Pickup · 6:40 PM",
        "PICKUP · 6:40 PM · PHONE ORDER",
        OrderChannel.Phone,
        Fulfillment.Pickup,
        listOf(QueueLine("Spicy Tonkotsu", 2, 1_450), QueueLine("Pork Gyoza", 1, 700)),
    ),
    QueueOrder(
        "1041",
        "Dev P.",
        OpenOrderStatus.Cooking,
        "Cooking · Delivery · 6:35 PM",
        "DELIVERY · 6:35 PM · PHONE ORDER",
        OrderChannel.Phone,
        Fulfillment.Delivery,
        listOf(QueueLine("Shoyu Ramen", 1, 1_300), QueueLine("Edamame", 1, 550)),
    ),
    QueueOrder(
        "1039",
        "Tara K.",
        OpenOrderStatus.Ready,
        "Ready · Pickup · 6:28 PM",
        "PICKUP · 6:28 PM · PHONE ORDER",
        OrderChannel.Phone,
        Fulfillment.Pickup,
        listOf(QueueLine("Miso Ramen", 3, 1_350)),
    ),
    QueueOrder(
        "1038",
        "Sam W.",
        OpenOrderStatus.Cooking,
        "Cooking · Pickup · 6:25 PM",
        "PICKUP · 6:25 PM · IN-STORE",
        OrderChannel.InStore,
        Fulfillment.Pickup,
        listOf(QueueLine("Pickup order", 1, 1_471)),
    ),
)

fun queueOrder(number: String): QueueOrder = QueueOrders.first { it.number == number }

enum class BoardFilter(@param:StringRes val labelRes: Int, val status: OpenOrderStatus?, val dueOnly: Boolean = false) {
    All(R.string.checkout_filter_all, null),
    New(R.string.checkout_filter_new, OpenOrderStatus.New),
    Cooking(R.string.checkout_filter_cooking, OpenOrderStatus.Cooking),
    Ready(R.string.checkout_filter_ready, OpenOrderStatus.Ready),
    Completed(R.string.checkout_filter_completed, OpenOrderStatus.Completed),
    Due(R.string.checkout_filter_due, null, dueOnly = true),
}

data class BoardLine(
    val id: String,
    val name: String,
    val quantity: Int,
    val lineCents: Int,
    val details: List<Int> = emptyList(),
    val mutedDetail: Int? = null,
    val collapsedRes: Int? = null,
)

data class BoardOrder(
    val number: String,
    val guest: String,
    val detailGuest: String = guest,
    val status: OpenOrderStatus,
    val fulfillment: Fulfillment,
    val time: String,
    val lines: List<BoardLine> = emptyList(),
    val subtotalCents: Int,
    val due: Boolean = false,
    val addressLine: String? = null,
    val expanded: Set<String> = emptySet(),
) {
    val taxCents: Int get() = taxCents(subtotalCents)
    val totalCents: Int get() = subtotalCents + taxCents
}

val DefaultBoard = listOf(
    BoardOrder(
        "1043",
        "Maya R.",
        status = OpenOrderStatus.Cooking,
        fulfillment = Fulfillment.Delivery,
        time = "6:57 PM",
        subtotalCents = 1_850,
        due = true,
    ),
    BoardOrder(
        "1042",
        "Giselle B.",
        detailGuest = "Maya R.",
        status = OpenOrderStatus.New,
        fulfillment = Fulfillment.Pickup,
        time = "6:40 PM",
        subtotalCents = 3_600,
        expanded = setOf("spicy"),
        lines = listOf(
            BoardLine(
                "spicy",
                "Spicy Tonkotsu",
                2,
                2_900,
                details = listOf(R.string.checkout_detail_richness, R.string.checkout_detail_spice),
                mutedDetail = R.string.checkout_detail_note,
            ),
            BoardLine("gyoza", "Pork Gyoza", 1, 700, collapsedRes = R.string.checkout_one_modifier),
        ),
    ),
    BoardOrder(
        "1041",
        "Dev P.",
        status = OpenOrderStatus.Cooking,
        fulfillment = Fulfillment.Delivery,
        time = "6:35 PM",
        subtotalCents = 1_850,
    ),
    BoardOrder(
        "1039",
        "Tara K.",
        status = OpenOrderStatus.Ready,
        fulfillment = Fulfillment.Pickup,
        time = "6:28 PM",
        subtotalCents = 4_050,
    ),
    BoardOrder(
        "1038",
        "Sam W.",
        status = OpenOrderStatus.Cooking,
        fulfillment = Fulfillment.Pickup,
        time = "6:25 PM",
        subtotalCents = 1_471,
    ),
    BoardOrder(
        "1035",
        "Lin H.",
        status = OpenOrderStatus.Completed,
        fulfillment = Fulfillment.Delivery,
        time = "6:10 PM",
        subtotalCents = 3_053,
    ),
    BoardOrder(
        "1034",
        "Counter",
        status = OpenOrderStatus.Completed,
        fulfillment = Fulfillment.Pickup,
        time = "6:05 PM",
        subtotalCents = 1_149,
    ),
)

fun phoneTail(phone: String): String = phone.filter(Char::isDigit).takeLast(4)

val OpenOrders = listOf(
    OpenOrder("1042", "Maya R.", OpenOrderStatus.New, "2× Spicy Tonkotsu · 1× Pork Gyoza", "PICKUP · 6:40", 3_915),
    OpenOrder("1041", "Dev P.", OpenOrderStatus.Cooking, "1× Shoyu Ramen · 1× Edamame", "DELIVERY · 6:35", 2_012),
    OpenOrder("1039", "Tara K.", OpenOrderStatus.Ready, "3× Miso Ramen", "PICKUP · 6:28", 4_404),
)

fun modifierGroups(): List<ModifierGroup> = listOf(
    ModifierGroup(
        "richness",
        R.string.checkout_richness,
        single = true,
        options = listOf(
            ModifierOption("light", R.string.checkout_mod_light),
            ModifierOption("regular", R.string.checkout_mod_regular),
            ModifierOption("extra-rich", R.string.checkout_mod_extra_rich, 150),
        ),
    ),
    ModifierGroup(
        "spice",
        R.string.checkout_spice,
        single = true,
        options = listOf(
            ModifierOption("mild", R.string.checkout_mod_mild),
            ModifierOption("medium", R.string.checkout_mod_medium),
            ModifierOption("hot", R.string.checkout_mod_hot),
            ModifierOption("extra-hot", R.string.checkout_mod_extra_hot),
        ),
    ),
    ModifierGroup(
        "addons",
        R.string.checkout_addons,
        single = false,
        options = listOf(
            ModifierOption("chashu", R.string.checkout_mod_chashu, 350),
            ModifierOption("egg", R.string.checkout_mod_egg, 200),
            ModifierOption("noodles", R.string.checkout_mod_noodles, 250),
            ModifierOption("bamboo", R.string.checkout_mod_bamboo, 150),
            ModifierOption("nori", R.string.checkout_mod_nori, 100),
        ),
    ),
)

fun defaultModifiers(itemId: String): Set<String> = if (itemId == "spicy-tonkotsu") {
    setOf("regular", "medium", "chashu", "egg")
} else {
    setOf("regular", "medium")
}

fun unitPrice(item: MenuItem, selected: Set<String>): Int {
    val extras = modifierGroups().flatMap { it.options }.filter { it.id in selected }.sumOf { it.priceCents }
    return item.priceCents + extras
}

fun subtotalCents(lines: List<CartLine>): Int = lines.sumOf { it.unitCents * it.quantity }

fun taxCents(subtotal: Int): Int = (subtotal * TaxNumerator + RoundHalf) / TaxDenominator

enum class MenuListFilter(@param:StringRes val labelRes: Int) {
    All(R.string.checkout_filter_all),
    EightySixed(R.string.menu_filter_eightysixed),
}

enum class UnavailableUntil(@param:StringRes val labelRes: Int) {
    EndOfDay(R.string.menu_until_end_of_day),
    Hours24(R.string.menu_until_24_hours),
    Week(R.string.menu_until_week),
    Unknown(R.string.menu_until_unknown),
}

data class KitchenMenuItem(
    val id: String,
    val name: String,
    val description: String,
    val priceCents: Int,
    val category: MenuCategory,
    @param:DrawableRes val photoRes: Int,
    val stock: Int?,
    val available: Boolean,
    val modifierGroups: List<Int> = emptyList(),
    val unavailableUntil: UnavailableUntil? = null,
)

data class MenuDraft(
    val id: String,
    val name: String,
    val description: String,
    val priceText: String,
    val stockText: String,
    val trackStock: Boolean,
    val available: Boolean,
)

fun KitchenMenuItem.toDraft() = MenuDraft(
    id = id,
    name = name,
    description = description,
    priceText = formatMoney(priceCents),
    stockText = stock?.toString().orEmpty(),
    trackStock = stock != null,
    available = available,
)

fun parseMenuPrice(raw: String): Int = raw.filter(Char::isDigit).toIntOrNull() ?: 0

val DefaultKitchenMenu = listOf(
    KitchenMenuItem(
        id = "spicy-tonkotsu",
        name = "Spicy Tonkotsu",
        description = "Rich pork broth, chili oil, chashu, egg",
        priceCents = 1_450,
        category = MenuCategory.Ramen,
        photoRes = R.drawable.menu_photo_spicy,
        stock = 24,
        available = true,
        modifierGroups = listOf(R.string.menu_mod_spice, R.string.menu_mod_addons, R.string.menu_mod_richness),
    ),
    KitchenMenuItem(
        id = "shoyu",
        name = "Shoyu Ramen",
        description = "",
        priceCents = 1_300,
        category = MenuCategory.Ramen,
        photoRes = R.drawable.menu_photo_shoyu,
        stock = null,
        available = true,
    ),
    KitchenMenuItem(
        id = "miso",
        name = "Miso Ramen",
        description = "",
        priceCents = 1_350,
        category = MenuCategory.Ramen,
        photoRes = R.drawable.menu_photo_miso,
        stock = null,
        available = true,
    ),
    KitchenMenuItem(
        id = "gyoza",
        name = "Pork Gyoza",
        description = "",
        priceCents = 700,
        category = MenuCategory.Sides,
        photoRes = R.drawable.menu_photo_gyoza,
        stock = null,
        available = true,
    ),
    KitchenMenuItem(
        id = "edamame",
        name = "Edamame",
        description = "",
        priceCents = 550,
        category = MenuCategory.Sides,
        photoRes = R.drawable.menu_photo_edamame,
        stock = null,
        available = false,
    ),
    KitchenMenuItem(
        id = "matcha",
        name = "Matcha Lemonade",
        description = "",
        priceCents = 500,
        category = MenuCategory.Drinks,
        photoRes = R.drawable.menu_photo_matcha,
        stock = null,
        available = true,
    ),
)

private val MenuGroupOrder = listOf(MenuCategory.Ramen, MenuCategory.Sides, MenuCategory.Drinks, MenuCategory.Donburi)

fun groupedMenu(items: List<KitchenMenuItem>): List<Pair<MenuCategory, List<KitchenMenuItem>>> = MenuGroupOrder.mapNotNull { category ->
    val group = items.filter { it.category == category }
    if (group.isEmpty()) null else category to group
}

fun parseDollars(text: String): Int {
    val cleaned = text.filter { it.isDigit() || it == '.' }
    if (cleaned.isEmpty()) return 0
    val parts = cleaned.split('.', limit = 2)
    val dollars = parts[0].toIntOrNull() ?: 0
    val cents = parts.getOrNull(1).orEmpty().filter(Char::isDigit).padEnd(2, '0').take(2).toIntOrNull() ?: 0
    return dollars * 100 + cents
}

fun moneyInput(cents: Int): String {
    val abs = kotlin.math.abs(cents)
    return "${abs / 100}.${(abs % 100).toString().padStart(2, '0')}"
}

fun formatMoney(cents: Int): String {
    val negative = cents < 0
    val abs = kotlin.math.abs(cents)
    val dollars = abs / 100
    val remainder = (abs % 100).toString().padStart(2, '0')
    val sign = if (negative) "−" else ""
    return "$sign$$dollars.$remainder"
}
