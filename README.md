# Compox2D

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![JitPack](https://jitpack.io/v/FelipeMz-dev/Compox2D.svg)](https://jitpack.io/#FelipeMz-dev/Compox2D)
[![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF.svg)](https://kotlinlang.org/docs/multiplatform.html)

Compox2D is a Kotlin Multiplatform 2D game engine for Android, Desktop, Web, and iOS. Its engine core is independent of UI frameworks, with Compose support provided as an optional integration module.

The project separates the reusable engine core from Compose-based rendering and input integration.

## Features

- Kotlin Multiplatform architecture
- Shared core logic across platforms
- Cross-platform game engine design
- Compose-based UI and rendering integration
- Box2D support for physics
- JitPack-ready Maven publishing setup

## Project structure

- `core/` — platform-independent engine and physics APIs; has no Compose dependency
- `compose/` — optional Compose rendering, input, and platform resource adapters
- `samples/` — demo/example code for local experiments; not published with the library
- `androidApp/` — Android application target
- `desktopApp/` — JVM/Desktop app target
- `webApp/` — Web app target
- `iosApp/` — iOS app target
- `build.gradle.kts` — root Gradle configuration
- `settings.gradle.kts` — project settings
- `LICENSE` — MIT license

## Installation

Add JitPack to your repositories:

```gradle
allprojects {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then add the dependency:

```kotlin
dependencies {
    implementation("com.github.FelipeMz-dev:core:v0.1.0")
}
```

To use the Compose renderer, add the integration module instead; it exposes the core transitively:

```kotlin
dependencies {
    implementation("com.github.FelipeMz-dev:compose:v0.1.0")
}
```

## Quick start

### Clone the repository

```bash
git clone https://github.com/FelipeMz-dev/Compox2D.git
cd Compox2D
```

### Build the project

```bash
./gradlew build
```

### Run the apps

Android:

```bash
./gradlew :androidApp:assembleDebug
```

Desktop:

```bash
./gradlew :desktopApp:run
```

Web (JavaScript target):

```bash
./gradlew :webApp:jsBrowserDevelopmentRun
```

iOS:

Open the `iosApp` directory in Xcode and run it from there.

## Development notes

Engine logic and framework-neutral image/input contracts live in `core`; Compose UI and Compose-backed platform implementations live in `compose`. This keeps core consumers independent of Compose while preserving the Compose game view for apps.

Samples and demos live outside the published artifact in the `samples/` folder, while app launch code remains in the platform-specific modules.

### Library hygiene

- Keep `core/` independent of Compose and app entry points.
- Keep `compose/` limited to the optional Compose integration.
- Keep platform-specific launch code in the local app modules (`androidApp/`, `desktopApp/`, `webApp/`, `iosApp/`).
- Use `samples/` or `examples/` for demo scenes if you want to keep the app targets smaller and more focused.

## Publishing a new version

To publish a new JitPack release:

```bash
git add .
git commit -m "Prepare release v0.1.0"
git tag v0.1.0
git push origin main --tags
```

JitPack will build automatically from the tag.

## License

This project is licensed under the [MIT License](LICENSE).

## Related technologies

- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform)
- [Kotlin/JS](https://kotlinlang.org/docs/js-overview.html)
- [Box2D](https://box2d.org/)

## Contributing

Contributions, bug reports, and feature requests are welcome. Please open an issue or submit a pull request with a clear description of the change.
