// Location: app/.../MainActivity.kt
// Function: Single-Activity Edge-to-Edge Compose host wiring WeatherViewModel
// and explicit one-shot coarse location permission without background tracking.
// All Rights Reserved isgaar

package mx.isgaar.myweather

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.isgaar.myweather.data.local.DeviceLocationFinder
import mx.isgaar.myweather.domain.model.WeatherCondition
import mx.isgaar.myweather.ui.motion.AppleMotionSpecs
import mx.isgaar.myweather.ui.screen.WeatherMainScreen
import mx.isgaar.myweather.ui.state.WeatherViewModel
import mx.isgaar.myweather.ui.theme.MyWeatherTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: WeatherViewModel = viewModel()
            val state by vm.state.collectAsState()
            val ctx = LocalContext.current
            val reducedMotion = remember {
                AppleMotionSpecs.isReducedMotionEnabled(ctx)
            }

            val locLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    val found = DeviceLocationFinder.readLastKnownLocation(ctx)
                    if (found != null) {
                        vm.selectLocation(found)
                    } else {
                        vm.setLocationNotice(R.string.locations_gps_unavailable)
                    }
                } else {
                    vm.setLocationNotice(R.string.locations_gps_denied)
                }
            }

            val snap = state.snapshot
            MyWeatherTheme(
                condition = snap?.current?.condition ?: WeatherCondition.CLEAR,
                isDay = snap?.current?.isDay ?: false
            ) {
                WeatherMainScreen(
                    state = state,
                    reducedMotion = reducedMotion,
                    onOpenLocations = { vm.setLocationSheet(true) },
                    onCloseLocations = { vm.setLocationSheet(false) },
                    onOpenPrivacy = { vm.setPrivacyDialog(true) },
                    onClosePrivacy = { vm.setPrivacyDialog(false) },
                    onToggleUnit = vm::toggleUnit,
                    onRefresh = vm::refreshWeather,
                    onQueryChange = vm::updateSearchQuery,
                    onSearch = vm::submitSearch,
                    onSelectLocation = vm::selectLocation,
                    onUseDeviceLocation = {
                        if (DeviceLocationFinder.hasCoarsePermission(ctx)) {
                            val found = DeviceLocationFinder
                                .readLastKnownLocation(ctx)
                            if (found != null) {
                                vm.selectLocation(found)
                            } else {
                                vm.setLocationNotice(
                                    R.string.locations_gps_unavailable
                                )
                            }
                        } else {
                            locLauncher.launch(
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        }
                    }
                )
            }
        }
    }
}
