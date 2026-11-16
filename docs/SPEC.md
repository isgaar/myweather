# Spec: MyWeather Android App and Home Screen Widgets

## Objective
Build a privacy-first, zero-telemetry Android weather application and suite of
home screen widgets starting from a user-established location, defaulting to
Orizaba, Veracruz, Mexico (18.85195, -97.09957, ~1,238 m elevation).
The UI draws inspiration from the three provided reference designs:
1. Clean 2x2 and 4x2 Android-style widgets (dynamic sky and light minimal).
2. Deep atmospheric gradient main screen with large tabular temperature,
   custom vector celestial/weather art, glassmorphic hourly curve card,
   local atmospheric highlights pager, and yesterday/daily forecast rows.
3. Translucent 2x2 solar horizon widget (sine-wave curve), 2x2 detail card,
   4x2 hourly strip, and 4x4 forecast widget with temperature range bars.

## Tech Stack
- Language: Kotlin (Java 17 bytecode, Android Studio JBR)
- UI: Jetpack Compose (BOM 2024.09.03, Material 3, Canvas vector art)
- Widgets: Android AppWidgetProvider + RemoteViews + Canvas Bitmap renderers
- Network: OkHttp 4.12.0 + org.json (strict HTTPS, zero telemetry/analytics)
- Async: Kotlin Coroutines 1.8.1 + Work/Alarm-free on-demand/periodic widget
  update via AppWidgetManager (`updatePeriodMillis`) and manual refresh
- Weather Provider: Open-Meteo Forecast, Air Quality, and Geocoding APIs
  (no API key, no tracking, multi-model blend suited for Sierra Madre Oriental)
- Build: Gradle 9.6.0, AGP 9.4.1, compileSdk 34, minSdk 26, targetSdk 34

## Commands
- Build APK: `JAVA_HOME=/home/ismael/Descargas/android-studio/jbr ./gradlew :app:assembleDebug`
- Unit Tests: `JAVA_HOME=/home/ismael/Descargas/android-studio/jbr ./gradlew :app:testDebugUnitTest`
- Install on Emulator: `/home/ismael/Android/Sdk/platform-tools/adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk`
- Launch App: `/home/ismael/Android/Sdk/platform-tools/adb -s emulator-5554 shell am start -n mx.isgaar.myweather/.MainActivity`

## Project Structure (Max 6 files per folder, Rule 120/80)
- `app/src/main/java/mx/isgaar/myweather/`
  - `domain/model/` — Pure Kotlin models (WeatherSnapshot, HourlyPoint,
    DailyForecast, SavedLocation, WeatherCondition, AirQualitySnapshot)
  - `domain/port/` — Repository & format helpers (`WeatherRepository`,
    `LocationGeocoder`, `UnitFormatter`, `WeatherError`)
  - `data/remote/` — Open-Meteo HTTPS clients & strict JSON boundary parsers
  - `data/local/` — SharedPreferences snapshot & established-location store
  - `ui/theme/` — XML-backed `MyWeatherTheme`, dynamic atmospheric palettes
  - `ui/motion/` — Apple-inspired interruptible spring & reduced-motion helpers
  - `ui/components/` — Custom Canvas vector icons, hero art, hourly curve,
    range bar, glass card
  - `ui/screen/` — Main weather screen, location picker sheet, settings sheet
  - `ui/state/` — `WeatherViewModel` & immutable `WeatherUiState`
  - `widget/data/` — Widget refresh coordinator & shared snapshot reader
  - `widget/render/` — Canvas bitmap & RemoteViews builders for all 4 widgets
  - `widget/provider/` — 4 `AppWidgetProvider` classes + refresh receiver
- `app/src/main/res/`
  - `values/strings.xml`, `values-en/strings.xml` (1:1 parity, zero Spanglish)
  - `values/colors.xml`, `values/themes.xml`
  - `layout/`, `drawable/`, `xml/` (widget layouts and `appwidget-provider`)
- `app/src/test/java/mx/isgaar/myweather/` — Unit tests (TDD)

## Code Style and MCP Norms
- 120/80 Rule: <= 120 lines per source file, <= 80 characters per line.
- Folder limit: <= 6 files per directory.
- Mandatory header on every `.kt` and `.kts` file:
  `// Location: ...`, `// Function: ...` (>= 100 chars), `// All Rights Reserved isgaar`.
- Zero emojis anywhere in code, UI, strings, logs, or tests.
- Zero hardcoded UI strings or hardcoded `Color(0xFF...)` in Compose code.
- Lifecycle: `ComponentCallbacks2` (`onTrimMemory`/`onLowMemory`), no Activity leaks.
- MCP Isolation: Keep all notes/docs inside `myweather`; do not write to `dopamina`.

## Boundaries
- Always: Validate external JSON strictly; enforce HTTPS; run unit tests and
  120/80 + folder-count audits before completion; verify visually on emulator.
- Ask first: Adding paid APIs, Firebase/analytics, or background GPS tracking.
- Never: Collect telemetry, hardcode secrets, commit API keys, or use emojis.

## Success Criteria
1. App launches on `emulator-5554` showing live Orizaba, Ver. weather without
   telemetry or mandatory GPS permissions.
2. User can search and establish any location or use Orizaba/Altas Montanas
   presets, toggle C/F, and view hourly curve, solar horizon, highlights, and
   yesterday + 7-day forecast with temperature range bars.
3. Four distinct widgets (Dynamic 2x2, Minimal 2x2, Solar Horizon 2x2, Forecast
   4x2/4x4) appear in the Android widget picker, render live data, and support
   tap-to-open and tap-to-refresh.
4. All unit tests pass, 120/80 and <= 6 files/folder checks pass with 0 violations.

