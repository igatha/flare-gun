package com.nizarmah.igatha

import android.os.ParcelUuid

object Constants {
    val SOS_BEACON_SERVICE_UUID: ParcelUuid = ParcelUuid.fromString("00001802-0000-1000-8000-00805F9B34FB") // 1802

    // grace period (seconds) before an incident response is triggered
    const val DISASTER_RESPONSE_GRACE_PERIOD: Double = 120.0

    // TODO: Add remaining constants
}
