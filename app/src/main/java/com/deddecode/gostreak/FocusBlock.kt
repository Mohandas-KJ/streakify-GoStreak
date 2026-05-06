package com.deddecode.gostreak

import java.util.UUID

data class FocusBlock(
    val id: String = UUID.randomUUID().toString(),
    val blockNumber: Int,
    val durationMinutes: Int,
    val timeLabel: String = "$durationMinutes Minutes",
    val timestamp: Long = System.currentTimeMillis()
)