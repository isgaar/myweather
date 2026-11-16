// Location: app/.../ui/motion/AppleMotionSpecs.kt
// Function: Centralizes interruptible spring physics and OS reduced-motion
// detection aligned with Apple fluid interface guidelines.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.motion

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring

object AppleMotionSpecs {

    fun <T> uiSpring(reducedMotion: Boolean): SpringSpec<T> {
        return if (reducedMotion) {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = 1200f
            )
        } else {
            spring(
                dampingRatio = 1.0f,
                stiffness = Spring.StiffnessMediumLow
            )
        }
    }

    fun <T> sheetSpring(reducedMotion: Boolean): SpringSpec<T> {
        return if (reducedMotion) {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = 1200f
            )
        } else {
            spring(
                dampingRatio = 0.85f,
                stiffness = Spring.StiffnessMedium
            )
        }
    }

    fun isReducedMotionEnabled(context: Context): Boolean {
        val scale = runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
        }.getOrDefault(1.0f)
        return scale <= 0.01f
    }
}
