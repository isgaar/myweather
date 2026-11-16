// Location: app/.../data/local/SnapshotJsonCodec.kt
// Function: Coordinates offline JSON encoding and decoding of WeatherSnapshot
// structures for zero-telemetry local storage and widget sharing.
// All Rights Reserved isgaar

package mx.isgaar.myweather.data.local

import mx.isgaar.myweather.domain.model.WeatherSnapshot

object SnapshotJsonCodec {

    fun encode(snapshot: WeatherSnapshot): String {
        return SnapshotJsonEncoder.encode(snapshot)
    }

    fun decode(raw: String?): WeatherSnapshot? {
        if (raw.isNullOrBlank()) return null
        return try {
            SnapshotJsonDecoder.decode(raw)
        } catch (_: Exception) {
            null
        }
    }
}
