package com.noshitechinc.restaurant.feature.role

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class DeviceRoleStore @Inject constructor() {
    private val _role = MutableStateFlow<DeviceRole?>(null)
    val role: StateFlow<DeviceRole?> = _role.asStateFlow()

    fun setRole(role: DeviceRole) {
        _role.value = role
    }

    fun clear() {
        _role.value = null
    }
}
