package com.noshitechinc.restaurant.feature.checkout

import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.quantity.KeypadKey
import com.noshitechinc.restaurant.core.designsystem.component.quantity.KeypadMode
import com.noshitechinc.restaurant.core.designsystem.component.quantity.KeypadReducer

enum class SettingsTab { Station, Delivery, Orders, Prep, Catering, Clock, Hours }

enum class SettingsDetail { None, Printer, Wifi }

enum class SettingsDialog {
    None,
    RemoveZone,
    Pin,
    ClockedOut,
    ClockedIn,
    Staff,
    EditStore,
    EditOnline,
    AddSpecial,
    EditSpecial,
    RemoveSpecial,
}

data class DeliveryZone(val id: String, val name: String, val fromMi: String, val toMi: String, val fee: String)

data class DayHours(val fullRes: Int, val shortRes: Int, val open: Boolean, val opens: String, val closes: String)

data class StaffShift(
    val id: String,
    val name: String,
    val pin: String,
    val onShift: Boolean,
    val since: String,
    val listValue: String,
    val resultIn: String,
    val resultOut: String,
    val today: String,
    val week: String,
    val elapsed: String,
    val lastShift: String,
)

data class SpecialHour(
    val id: String,
    val label: String,
    val date: String,
    val name: String,
    val closed: Boolean,
    val opens: String,
    val closes: String,
)

data class SettingsState(
    val tab: SettingsTab = SettingsTab.Station,
    val detail: SettingsDetail = SettingsDetail.None,
    val dialog: SettingsDialog = SettingsDialog.None,
    val requirePasscode: Boolean = true,
    val autoLock: Boolean = true,
    val autoPrint: Boolean = true,
    val optionNames: Boolean = false,
    val newOrderAlert: Boolean = true,
    val readyAlert: Boolean = true,
    val offlineAlert: Boolean = false,
    val blockOffline: Boolean = true,
    val autoUpdate: Boolean = true,
    val printerPaired: Boolean = true,
    val kitchenTickets: Boolean = true,
    val copies: Int = 3,
    val wifiOn: Boolean = true,
    val radius: String = "3.2",
    val minimumOrder: String = "15.00",
    val deliveryFee: String = "3.99",
    val zones: List<DeliveryZone> = DefaultZones,
    val pendingZoneId: String? = null,
    val acceptPickup: Boolean = true,
    val acceptDelivery: Boolean = true,
    val allowScheduled: Boolean = true,
    val autoAccept: Boolean = true,
    val defaultFulfillment: Fulfillment = Fulfillment.Pickup,
    val pinOpenOrders: Boolean = true,
    val prepMinutes: String = "18",
    val maxOrders: String = "12",
    val autoBusy: Boolean = true,
    val pauseOnline: Boolean = false,
    val leadHours: String = "24",
    val windowDays: String = "7",
    val reviewCatering: Boolean = false,
    val staff: List<StaffShift> = DefaultStaff,
    val pin: String = "",
    val clockStaffId: String? = null,
    val storeDays: List<DayHours> = DefaultStoreDays,
    val storeDraft: List<DayHours> = DefaultStoreDays,
    val onlineDays: List<DayHours> = DefaultOnlineDays,
    val onlineDraft: List<DayHours> = DefaultOnlineDays,
    val onlineMatchesStore: Boolean = true,
    val onlineDraftMatches: Boolean = true,
    val specials: List<SpecialHour> = DefaultSpecials,
    val specialDraft: SpecialHour = BlankSpecial,
    val pendingSpecialId: String? = null,
)

private val DefaultZones = listOf(
    DeliveryZone("zone-a", "Zone A", "0", "2", "2.99"),
    DeliveryZone("zone-b", "Zone B", "2", "4", "4.99"),
)

private val DefaultStoreDays = listOf(
    DayHours(R.string.settings_mon, R.string.settings_short_mon, true, "11:00 AM", "9:00 PM"),
    DayHours(R.string.settings_tue, R.string.settings_short_tue, true, "11:00 AM", "9:00 PM"),
    DayHours(R.string.settings_wed, R.string.settings_short_wed, true, "11:00 AM", "9:00 PM"),
    DayHours(R.string.settings_thu, R.string.settings_short_thu, true, "11:00 AM", "9:00 PM"),
    DayHours(R.string.settings_fri, R.string.settings_short_fri, true, "11:00 AM", "12:00 AM"),
    DayHours(R.string.settings_sat, R.string.settings_short_sat, true, "11:00 AM", "12:00 AM"),
    DayHours(R.string.settings_sun, R.string.settings_short_sun, true, "11:00 AM", "10:00 PM"),
)

private val DefaultStaff = listOf(
    StaffShift("maya", "Maya R.", "2205", true, "2:05 PM", "4h 12m", "2:05 PM", "6:23 PM", "4h 18m", "32h 06m", "4h 18m", "1:15 PM"),
    StaffShift("dev", "Dev P.", "3300", true, "3:30 PM", "2h 47m", "3:30 PM", "6:23 PM", "2h 47m", "18h 10m", "2h 47m", "11:40 AM"),
    StaffShift("tara", "Tara K.", "1115", false, "1:15 PM", "—", "4:42 PM", "1:15 PM", "6h 02m", "21h 40m", "6h 02m", "1:15 PM"),
    StaffShift("sam", "Sam W.", "1140", false, "11:40 AM", "—", "4:42 PM", "11:40 AM", "0h 00m", "8h 20m", "0h 00m", "11:40 AM"),
)

private val DefaultOnlineDays = listOf(
    DayHours(R.string.settings_mon, R.string.settings_short_mon, true, "11:00 AM", "8:30 PM"),
    DayHours(R.string.settings_tue, R.string.settings_short_tue, true, "11:00 AM", "8:30 PM"),
    DayHours(R.string.settings_wed, R.string.settings_short_wed, true, "11:00 AM", "8:30 PM"),
    DayHours(R.string.settings_thu, R.string.settings_short_thu, true, "11:00 AM", "8:30 PM"),
    DayHours(R.string.settings_fri, R.string.settings_short_fri, true, "11:00 AM", "11:30 PM"),
    DayHours(R.string.settings_sat, R.string.settings_short_sat, true, "11:00 AM", "11:30 PM"),
    DayHours(R.string.settings_sun, R.string.settings_short_sun, true, "11:00 AM", "9:30 PM"),
)

private val DefaultSpecials = listOf(
    SpecialHour("thanksgiving", "Thu, Nov 26", "Nov 26, 2026", "Thanksgiving", true, "11:00 AM", "9:00 PM"),
    SpecialHour("christmas-eve", "Thu, Dec 24", "Dec 24, 2026", "Christmas Eve", false, "11:00 AM", "3:00 PM"),
)

internal val BlankSpecial = SpecialHour("draft", "Nov 26, 2026", "Nov 26, 2026", "Thanksgiving", true, "11:00 AM", "9:00 PM")

internal const val PIN_LENGTH = 4

fun SettingsState.afterPinKey(key: KeypadKey): SettingsState {
    val next = KeypadReducer.reduce(pin, key, KeypadMode.Pin, maxLength = PIN_LENGTH)
    if (next.length < PIN_LENGTH) return copy(pin = next, dialog = SettingsDialog.Pin)
    val match = staff.firstOrNull { it.pin == next } ?: return copy(pin = "")
    return copy(
        pin = "",
        clockStaffId = match.id,
        dialog = if (match.onShift) SettingsDialog.ClockedOut else SettingsDialog.ClockedIn,
        staff = staff.map { person -> if (person.id == match.id) person.toggled() else person },
    )
}

fun SettingsState.withStaffClocked(): SettingsState {
    val id = clockStaffId ?: return this
    return copy(dialog = SettingsDialog.None, staff = staff.map { person -> if (person.id == id) person.toggled() else person })
}

private fun StaffShift.toggled(): StaffShift = if (onShift) {
    copy(onShift = false, lastShift = resultOut, listValue = "—")
} else {
    copy(onShift = true, since = resultIn, listValue = elapsed)
}

fun SettingsState.withAddedZone(): SettingsState {
    val letter = 'A' + zones.size
    val from = zones.lastOrNull()?.toMi ?: "0"
    val toValue = (from.toDoubleOrNull() ?: 0.0) + 2.0
    val to = if (toValue % 1.0 == 0.0) toValue.toInt().toString() else toValue.toString()
    return copy(
        zones = zones + DeliveryZone("zone-$letter", "Zone $letter", from, to, ""),
    )
}

fun SettingsState.withoutPendingZone(): SettingsState =
    copy(zones = zones.filter { it.id != pendingZoneId }, dialog = SettingsDialog.None, pendingZoneId = null)

fun groupedHours(days: List<DayHours>): List<List<DayHours>> {
    if (days.isEmpty()) return emptyList()
    val groups = mutableListOf<MutableList<DayHours>>()
    days.forEach { day ->
        val current = groups.lastOrNull()
        val previous = current?.lastOrNull()
        if (previous != null && previous.open == day.open && previous.opens == day.opens && previous.closes == day.closes) {
            current.add(day)
        } else {
            groups.add(mutableListOf(day))
        }
    }
    return groups
}
