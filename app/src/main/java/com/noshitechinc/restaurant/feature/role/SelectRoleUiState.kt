package com.noshitechinc.restaurant.feature.role

import androidx.annotation.StringRes
import com.noshitechinc.restaurant.R

enum class DeviceRole(@param:StringRes val titleRes: Int, @param:StringRes val bodyRes: Int) {
    Staff(R.string.role_staff_title, R.string.role_staff_body),
    Pos(R.string.role_pos_title, R.string.role_pos_body),
    SelfService(R.string.role_self_title, R.string.role_self_body),
}

data class SelectRoleUiState(val selected: DeviceRole? = null)
