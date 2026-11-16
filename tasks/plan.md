# Implementation Plan: MyWeather

## Dependency Graph
1. Gradle & Android Manifest + XML Resources (`strings.xml`, `values-en/strings.xml`,
   `colors.xml`, `themes.xml`)
2. Domain Models & Unit Formatters (`domain/model`, `domain/port`) + Unit Tests
3. Remote Open-Meteo Clients & JSON Boundary Parsers (`data/remote`) + Unit Tests
4. Local Established-Location & Snapshot Cache (`data/local`) + Unit Tests
5. Atmospheric Theme, Apple Spring Motion, & Custom Canvas Vector Weather Art
   (`ui/theme`, `ui/motion`, `ui/components`)
6. Main Screen, Location Selector Sheet, & ViewModel (`ui/state`, `ui/screen`,
   `MainActivity`, `MyWeatherApp` with `ComponentCallbacks2`)
7. Home Screen Widgets: Renderers + Providers + Widget XMLs (`widget/*`, `res/xml/*`,
   `res/layout/*`)
8. Build, Unit Test Suite, 120/80 + Folder Audit, Emulator Install & Visual Check

## Risks and Mitigations
- Open-Meteo field missing or offline: validate JSON boundaries defensively with
  `ERR_*` codes and fall back to cached snapshot on disk.
- 120/80 & 6-files/folder rules: keep small single-purpose Kotlin files and split
  screens/widgets into focused subpackages before exceeding limits.
- RemoteViews bitmap memory on widgets: render compact bounded ARGB_8888 bitmaps
  with clean density scaling and recycle cleanly.

