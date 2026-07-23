# iosApp

This directory holds the Swift/iOS entry point for RelaxMarket. It was authored on Windows, where Xcode isn't available, so **the `.xcodeproj` itself is not included yet** — hand-writing `project.pbxproj` blind is too error-prone to be worth it. Generate it on macOS instead:

1. In Xcode, create a new **App** project (SwiftUI, iOS) named `iosApp`, saved into this `iosApp/` directory so it sits alongside the files below.
2. Replace the generated `iOSApp.swift`/`ContentView.swift`/`Info.plist` with the ones already in this folder (they call into the shared KMP framework).
3. Add a **Run Script** build phase that compiles the shared framework before Xcode links against it:
   ```sh
   cd "$SRCROOT/.."
   ./gradlew :app:embedAndSignAppleFrameworkForXcode
   ```
4. Add `$(SRCROOT)/../app/build/xcode-frameworks` (or wherever the above task emits it) to **Framework Search Paths**.
5. The framework is named `shared` (see `binaries.framework { baseName = "shared" }` in `app/build.gradle.kts`), matching the `import shared` in `ContentView.swift`.

Once the `.xcodeproj` exists, `iosApp.xcodeproj` should be added to git normally.
