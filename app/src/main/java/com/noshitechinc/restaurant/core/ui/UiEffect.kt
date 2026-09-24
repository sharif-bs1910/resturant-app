package com.noshitechinc.restaurant.core.ui

import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.core.common.UiText

interface UiEffect

enum class MessageTone { Info, Success, Error }

data class ShowMessage(val text: UiText, val tone: MessageTone = MessageTone.Info) : UiEffect

data class ShowErrorDialog(val error: AppError) : UiEffect
