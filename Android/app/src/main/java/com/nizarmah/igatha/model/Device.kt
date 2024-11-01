package com.nizarmah.igatha.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

fun UUID.shortName(): String {
    return this.toString().substring(0, 8).uppercase()
}

@Parcelize
data class Device(
    val id: String,
    val shortName: String,
    var rssi: Double,
    var lastSeen: Date = Date()
) : Parcelable {
    constructor(
        id: UUID,
        rssi: Double,
        lastSeen: Date = Date()
    ) : this(
        id.toString().uppercase(),
        id.shortName(),
        rssi,
        lastSeen
    )

    fun update(rssi: Double, lastSeen: Date = Date()) {
        val oldRSSI = this.rssi
        val newRSSI = rssi

        // smoothing factor
        // TODO: Replace with constant
        val alpha = 0.18

        // smoothen the RSSI with exponential moving average
        val smoothedRSSI = alpha * newRSSI + (1 - alpha) * oldRSSI

        this.rssi = smoothedRSSI
        this.lastSeen = lastSeen
    }

    fun estimateDistance(pathLossExponent: PathLossExponent = PathLossExponent.URBAN): Double {
        // 1 meter ~= -59.0 RSSI
        val txPower = -59.0

        // path-loss exponent
        val n: Double = pathLossExponent.value

        val distance = 10.0.pow((txPower - rssi) / (10.0 * n))

        // round for simplicity
        return (distance * 1000.0).roundToInt() / 1000.0
    }
}

enum class PathLossExponent(val value: Double) {
    // path-loss exponent (n)

    // free open spaces
    // n = 2.0
    FREE_SPACE(2.0),

    // indoor environments
    // n = 3.0
    INDOOR(3.0),

    // dense urban environments
    // n = 4.0
    URBAN(4.0)
}
