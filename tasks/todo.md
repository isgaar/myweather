# Task Checklist: MyWeather

- [x] Task 1: Scaffold Gradle project, AndroidManifest, and localized XML resources
  - Verified: `:app:assembleDebug` passes; `strings.xml` / `values-en/strings.xml` 1:1 parity (77 keys)
- [x] Task 2: TDD Domain models, WMO condition mapper, and temperature/solar math
  - Verified: `DomainMathTest` (4 tests) passes in `:app:testDebugUnitTest`
- [x] Task 3: TDD Open-Meteo HTTPS forecast, air-quality, and geocoding parsers
  - Verified: `OpenMeteoParserTest` (3 tests) passes in `:app:testDebugUnitTest`
- [x] Task 4: Local established-location & cached-snapshot store + Application lifecycle
  - Verified: `SnapshotJsonCodec` round-trip passes; `MyWeatherApp` trim-memory lifecycle verified with zero crashes on `emulator-5554`
- [x] Task 5: Theme, spring motion, and Canvas vector art (icons, moon/sun hero, curve, bars)
  - Verified: Rendered cleanly on `emulator-5554` (`verify_main_top.png`, `verify_main_bottom.png`)
- [x] Task 6: Main Weather Screen, Location Picker Sheet, Privacy Dialog, and ViewModel
  - Verified: App runs on `emulator-5554` with live Orizaba weather (`1234 m elevation`, `Open-Meteo`)
- [x] Task 7: Implement 4 Home Screen Widgets (Dynamic 2x2, Minimal 2x2, Solar 2x2, Forecast 4x2/4x4)
  - Verified: All 4 providers registered in `AppWidgetService`, inspected in Widget Picker, and live widgets (`Dynamic`, `Solar`, `Forecast`) verified on Home screen
- [x] Task 8: Audit 120/80, <= 6 files/folder, zero emojis, full tests, and emulator screenshots
  - Verified: 0 violations (`<=120` lines/file, `<=80` chars/line, `<=6` files/folder, 3-line headers), 7/7 unit tests pass, zero telemetry SDKs, zero fatal logcat exceptions
