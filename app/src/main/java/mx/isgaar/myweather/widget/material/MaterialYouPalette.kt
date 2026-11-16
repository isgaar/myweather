// Location: app/.../widget/material/MaterialYouPalette.kt
// Function: Resolves dynamic Android 12+ Material You Monet system color
// tokens with light/dark awareness and expressive Material 3 fallbacks.
// All Rights Reserved isgaar

package mx.isgaar.myweather.widget.material

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.core.content.ContextCompat

data class YouPalette(
    val surfaceTop: Int,
    val surfaceBot: Int,
    val badgeBg: Int,
    val chipBg: Int,
    val onChip: Int,
    val fg: Int,
    val sub: Int,
    val border: Int
)

object MaterialYouPalette {

    fun resolve(ctx: Context): YouPalette {
        val night = (ctx.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return fallback(night)
        }
        val c = { id: Int -> ContextCompat.getColor(ctx, id) }
        return if (night) {
            YouPalette(
                surfaceTop = c(android.R.color.system_neutral1_800),
                surfaceBot = c(android.R.color.system_neutral1_900),
                badgeBg = c(android.R.color.system_accent2_700),
                chipBg = c(android.R.color.system_accent1_200),
                onChip = c(android.R.color.system_neutral1_900),
                fg = c(android.R.color.system_neutral1_50),
                sub = c(android.R.color.system_neutral2_200),
                border = (c(android.R.color.system_accent1_200) and 0x29FFFFFF)
            )
        } else {
            YouPalette(
                surfaceTop = c(android.R.color.system_accent2_50),
                surfaceBot = c(android.R.color.system_accent2_100),
                badgeBg = c(android.R.color.system_accent2_200),
                chipBg = c(android.R.color.system_accent1_100),
                onChip = c(android.R.color.system_accent1_900),
                fg = c(android.R.color.system_neutral1_900),
                sub = c(android.R.color.system_neutral2_700),
                border = (c(android.R.color.system_neutral1_900) and 0x1EFFFFFF)
            )
        }
    }

    private fun fallback(night: Boolean): YouPalette = if (night) {
        YouPalette(
            surfaceTop = 0xFF282B34.toInt(),
            surfaceBot = 0xFF1E2028.toInt(),
            badgeBg = 0xFF394256.toInt(),
            chipBg = 0xFFD0BCFF.toInt(),
            onChip = 0xFF1C1B1F.toInt(),
            fg = 0xFFF4F3F8.toInt(),
            sub = 0xFFCAC4D0.toInt(),
            border = 0x26FFFFFF
        )
    } else {
        YouPalette(
            surfaceTop = 0xFFF3EDF7.toInt(),
            surfaceBot = 0xFFE8DEF8.toInt(),
            badgeBg = 0xFFD8CEEB.toInt(),
            chipBg = 0xFFEADDFF.toInt(),
            onChip = 0xFF21005D.toInt(),
            fg = 0xFF1D1B20.toInt(),
            sub = 0xFF49454F.toInt(),
            border = 0x1E1D1B20
        )
    }

    fun isNight(ctx: Context): Boolean =
        (ctx.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}
