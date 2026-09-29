# Compox2D

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![JitPack](https://jitpack.io/v/FelipeMz-dev/Compox2D.svg)](https://jitpack.io/#FelipeMz-dev/Compox2D)
[![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF.svg)](https://kotlinlang.org/docs/multiplatform.html)

Compox2D is a Kotlin Multiplatform 2D game engine and framework designed to run across Android, Desktop, Web, and iOS using shared game logic and Compose-based rendering.

The project is organized around a reusable `shared` module that contains the engine core, while platform-specific entry points such as Android, Desktop, Web, and iOS remain isolated in their respective app modules.

## Features

- Kotlin Multiplatform architecture
- Shared core logic across platforms
- Cross-platform game engine design
- Compose-based UI and rendering integration
- Box2D support for physics
- JitPack-ready Maven publishing setup

## Project structure

- `shared/` — shared engine/core module
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

```gradle
dependencies {
    implementation 'com.github.FelipeMz-dev:Compox2D:v0.1.0'
}
```

Kotlin DSL:

```kotlin
dependencies {
    implementation("com.github.FelipeMz-dev:Compox2D:v0.1.0")
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

Web (Wasm target):

```bash
./gradlew :webApp:wasmJsBrowserDevelopmentRun
```

iOS:

Open the `iosApp` directory in Xcode and run it from there.

## Development notes

This project is structured so that the engine logic remains in the `shared` module and platform-specific behavior stays in each target module. This makes the library easier to reuse and more portable across multiple environments.

### Library hygiene

- Keep `shared/` publication-safe: no app entry points, no `App()` classes, no `ComposeUIViewController { App() }` wrappers, and no UI bootstrap code.
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
- [Kotlin/Wasm](https://kotl.in/wasm/)
- [Box2D](https://box2d.org/)

## Contributing

Contributions, bug reports, and feature requests are welcome. Please open an issue or submit a pull request with a clear description of the change.
