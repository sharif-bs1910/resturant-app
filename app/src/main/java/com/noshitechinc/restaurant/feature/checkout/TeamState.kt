package com.noshitechinc.restaurant.feature.checkout

import androidx.annotation.StringRes
import com.noshitechinc.restaurant.R

enum class TeamDialog {
    None,
    Edit,
    Remove,
}

enum class TeamRole {
    Owner,
    Manager,
    KitchenLead,
    Kitchen,
    Foh,
}

data class TeamMember(
    val id: String,
    val initials: String,
    val name: String,
    val email: String,
    val role: TeamRole,
    val lastActive: String,
    val avatarIndex: Int,
    val inviteSent: Boolean = false,
    val pin: String = "",
)

data class TeamState(
    val members: List<TeamMember> = DefaultTeamMembers,
    val inviteOpen: Boolean = false,
    val inviteEmail: String = "",
    val inviteName: String = "",
    val inviteRole: TeamRole? = null,
    val invitePin: String = "",
    val inviteRoleMenuOpen: Boolean = false,
    val menuForId: String? = null,
    val dialog: TeamDialog = TeamDialog.None,
    val editingId: String? = null,
    val editEmail: String = "",
    val editName: String = "",
    val editRole: TeamRole = TeamRole.Kitchen,
    val editPin: String = "",
    val editRoleMenuOpen: Boolean = false,
) {
    val editingMember: TeamMember?
        get() = members.firstOrNull { it.id == editingId }

    val canCreateInvite: Boolean
        get() = inviteEmail.isNotBlank() &&
            inviteName.isNotBlank() &&
            inviteRole != null &&
            invitePin.filter { it.isDigit() }.length == TEAM_PIN_LENGTH
}

val InviteRoles: List<TeamRole> = listOf(
    TeamRole.Manager,
    TeamRole.KitchenLead,
    TeamRole.Kitchen,
    TeamRole.Foh,
)

const val TEAM_PIN_LENGTH = 4

@StringRes
fun TeamRole.badgeRes(): Int = when (this) {
    TeamRole.Owner -> R.string.team_role_owner
    TeamRole.Manager -> R.string.team_role_manager
    TeamRole.KitchenLead -> R.string.team_role_kitchen_lead
    TeamRole.Kitchen -> R.string.team_role_kitchen
    TeamRole.Foh -> R.string.team_role_foh
}

@StringRes
fun TeamRole.pickerRes(): Int = when (this) {
    TeamRole.Owner -> R.string.team_role_owner
    TeamRole.Manager -> R.string.team_role_picker_manager
    TeamRole.KitchenLead -> R.string.team_role_picker_kitchen_lead
    TeamRole.Kitchen -> R.string.team_role_picker_kitchen
    TeamRole.Foh -> R.string.team_role_picker_foh
}

fun TeamState.openInvite(): TeamState = copy(
    inviteOpen = true,
    menuForId = null,
    inviteRoleMenuOpen = false,
)

fun TeamState.closeInvite(): TeamState = copy(
    inviteOpen = false,
    inviteEmail = "",
    inviteName = "",
    inviteRole = null,
    invitePin = "",
    inviteRoleMenuOpen = false,
)

fun TeamState.createInvite(): TeamState {
    val role = inviteRole ?: return this
    if (!canCreateInvite) return this
    val name = inviteName.trim()
    val email = inviteEmail.trim()
    val member = TeamMember(
        id = "invite-${email.lowercase()}",
        initials = initialsFor(name),
        name = name,
        email = email,
        role = role,
        lastActive = "",
        avatarIndex = members.size % 6,
        inviteSent = true,
        pin = invitePin.filter { it.isDigit() }.take(TEAM_PIN_LENGTH),
    )
    val owner = members.firstOrNull { it.role == TeamRole.Owner }
    val rest = members.filterNot { it.role == TeamRole.Owner || it.id == member.id }
    val next = buildList {
        if (owner != null) add(owner)
        add(member)
        addAll(rest)
    }
    return copy(
        members = next,
        inviteEmail = "",
        inviteName = "",
        inviteRole = null,
        invitePin = "",
        inviteRoleMenuOpen = false,
        inviteOpen = false,
        menuForId = null,
    )
}

fun TeamState.openEdit(id: String): TeamState {
    val member = members.firstOrNull { it.id == id } ?: return copy(menuForId = null)
    return copy(
        menuForId = null,
        dialog = TeamDialog.Edit,
        editingId = member.id,
        editEmail = member.email,
        editName = member.name,
        editRole = member.role,
        editPin = member.pin.ifBlank { "2564" },
        editRoleMenuOpen = false,
        inviteRoleMenuOpen = false,
    )
}

fun TeamState.openRemove(id: String): TeamState {
    val member = members.firstOrNull { it.id == id } ?: return copy(menuForId = null)
    if (member.role == TeamRole.Owner) return copy(menuForId = null)
    return copy(
        menuForId = null,
        dialog = TeamDialog.Remove,
        editingId = member.id,
        inviteRoleMenuOpen = false,
        editRoleMenuOpen = false,
    )
}

fun TeamState.saveEdit(): TeamState {
    val id = editingId ?: return dismissDialog()
    val name = editName.trim()
    val email = editEmail.trim()
    if (name.isBlank() || email.isBlank()) return this
    val pin = editPin.filter { it.isDigit() }.take(TEAM_PIN_LENGTH)
    return copy(
        members = members.map { member ->
            if (member.id != id) {
                member
            } else {
                member.copy(
                    name = name,
                    email = email,
                    role = if (member.role == TeamRole.Owner) TeamRole.Owner else editRole,
                    pin = pin,
                    initials = initialsFor(name),
                )
            }
        },
        dialog = TeamDialog.None,
        editingId = null,
        editRoleMenuOpen = false,
    )
}

fun TeamState.confirmRemove(): TeamState {
    val id = editingId ?: return dismissDialog()
    val target = members.firstOrNull { it.id == id } ?: return dismissDialog()
    if (target.role == TeamRole.Owner) return dismissDialog()
    return copy(
        members = members.filterNot { it.id == id },
        dialog = TeamDialog.None,
        editingId = null,
    )
}

fun TeamState.dismissDialog(): TeamState = copy(
    dialog = TeamDialog.None,
    editingId = null,
    editRoleMenuOpen = false,
)

private fun initialsFor(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "${parts.first().first()}${parts.last().first()}".uppercase()
    }
}

val DefaultTeamMembers: List<TeamMember> = listOf(
    TeamMember("mimi", "MA", "Mimi Ahmadi", "mimi@mimispizza.com", TeamRole.Owner, "Now", 0, pin = "0000"),
    TeamMember("joseph", "JM", "Joseph Mwangi", "joe@mimispizza.com", TeamRole.Manager, "2 hr ago", 1, pin = "1234"),
    TeamMember("luca", "LR", "Luca Rossi", "luca.r@mimispizza.com", TeamRole.KitchenLead, "Today", 2, pin = "2564"),
    TeamMember("sofia", "SP", "Sofia Park", "sofia@mimispizza.com", TeamRole.Kitchen, "4 hr ago", 3, pin = "4321"),
    TeamMember("diana", "DK", "Diana Kim", "diana@mimispizza.com", TeamRole.Foh, "1 hr ago", 4, pin = "5678"),
    TeamMember("tom", "TN", "Tom Nguyen", "tom@mimispizza.com", TeamRole.Foh, "Yesterday", 5, pin = "9876"),
)
