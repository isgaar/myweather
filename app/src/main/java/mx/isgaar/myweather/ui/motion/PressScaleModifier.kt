// Location: app/.../ui/motion/PressScaleModifier.kt
// Function: Provides immediate touch-down scale feedback on interactive views
// without waiting for click release, supporting interruptible motion.
// All Rights Reserved isgaar

package mx.isgaar.myweather.ui.motion

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.pressScale(
    reducedMotion: Boolean = false,
    onClick: () -> Unit
): Modifier = composed {
    var pressed by remember { mutableStateOf(false) }
    val target = if (pressed && !reducedMotion) 0.96f else 1f
    val scale by animateFloatAsState(
        targetValue = target,
        animationSpec = AppleMotionSpecs.uiSpring(reducedMotion),
        label = "pressScale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(onClick) {
            detectTapGestures(
                onPress = {
                    pressed = true
                    tryAwaitRelease()
                    pressed = false
                },
                onTap = { onClick() }
            )
        }
}
