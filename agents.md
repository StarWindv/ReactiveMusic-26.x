# ReactiveMusic Agent Notes

ReactiveMusic is a cross-loader Minecraft mod that replaces vanilla music with dynamic, event-driven music and bundled/user songpacks.

## Layout

- `src/main/java/` and `src/main/resources/` are shared/common code and resources.
- `fabric/`, `forge/`, and `neoforge/` contain loader-specific setup.
- `versions/<mc version>/gradle.properties` contains per-version dependency values.
- `stonecutter.gradle.kts` controls the active Minecraft version and helper tasks.
- `run/<mc version>/<loader>/` contains isolated development instances.

## Stonecutter

This repo uses Stonecutter comments (`//? if ...`) for Minecraft-version differences.

Do not manually flip Stonecutter comment blocks to switch versions. Use the Stonecutter/Gradle tasks.

Common commands:

- Refresh active source state: `./gradlew "Refresh active project"`
- Run active Fabric client: `./gradlew runActiveClientFabric`
- Run active NeoForge client: `./gradlew runActiveClientNeoforge`
- Run NeoForge 1.21.1 directly: `./gradlew :neoforge:1.21.1:runClient`
- Build all supported artifacts: `./gradlew chiseledBuild`

## Building on this machine

Use these paths; the Gradle wrapper's shared home is outside the repo and the daemon
needs write access to it.

```
JAVA_HOME = E:\Java_JDK\J26
gradle    = E:\stv-tools\gradle-8.14.2\cache\wrapper\dists\gradle-9.6.1-bin\4ticwg1pgcbps2hj28r8so764\gradle-9.6.1\bin\gradle.bat
```

Other tasks are running on this machine, so keep builds small:
`-Dorg.gradle.jvmargs=-Xmx2560m --max-workers=2`.

### Build one target, not everything

`chiseledBuild` builds every version of every loader (10 artifacts, several minutes).
While developing, build only the target under test:

- `:fabric:26.2:buildAndCollect` → `build/libs/<version>/fabric/`
- `:forge:26.2:buildAndCollect` → `build/libs/<version>/forge/`
- `:neoforge:26.2:buildAndCollect` → `build/libs/<version>/neoforge/`

`buildAndCollect` is the task that copies the artifact into `build/libs`. Plain `build`
leaves it in `<loader>/versions/<mc>/build/libs/`. Reserve the full `chiseledBuild` for
release/commit checks — it is the only thing that proves the lower versions still compile.

### Chiseled sources go stale

Editing anything under `src/main/java/` does not always invalidate a single loader
project's generated sources in `<loader>/versions/<mc>/build/chiseledSrc`, so a
single-target build can silently compile the previous version of your change. Force it:

```
./gradlew :forge:26.2:buildAndCollect --rerun-tasks
```

When a build looks like it ignored your edit, this is why. `--no-build-cache` does not
help on its own; the generation step has to re-run.

## Committing

1. Run `./gradlew chiseledBuild --no-build-cache`.
2. Before staging, run `./gradlew "Reset active project"` to normalize sources to `1.19.2`.
3. Review `git status` and `git diff`, stage explicit paths, then review `git diff --cached` before committing.

## Runtime smoke testing

Before releases, run `powershell -ExecutionPolicy Bypass -File scripts/release-smoke-test.ps1`. It builds production artifacts, then uses cached HeadlessMC installations to run every supported client in parallel. Each client creates a world, waits for a loaded chunk, verifies ReactiveMusic starts a song, and exits automatically.

- `1.19.2`: Fabric and Forge
- `1.20.1`: Fabric and Forge
- `1.21.1`: Fabric and NeoForge
- `1.21.11`: Fabric

Finish with `./gradlew "Reset active project"` and `./gradlew chiseledBuild --no-build-cache`.

Supported targets:

- Fabric: `1.19.2`, `1.20.1`, `1.21.1`, `1.21.11`
- Forge: `1.19.2`, `1.20.1`
- NeoForge: `1.21.1`

## Underground detection

`SongPicker` decides the `UNDERGROUND` / `DEEP_UNDERGROUND` events, which drive the cave
music. Read this before changing it — three other approaches were tried and all failed.

The check reads sky light in the single block the player's head occupies:

```java
skyLightAt(world, playerPos.above())  <=  UNDERGROUND_MAX_HEAD_SKY_LIGHT   // 0
```

Measured sky light: open ground `15`, under a leaf block `14`, under a one-block-thick
roof `14`, inside a cave `0`. So `0` is the dividing line, and no tuning is needed.

Why not the obvious alternatives:

- **Sky visibility of the block the player stands in** (`canSeeSky` / `isSkyVisible`) is the
  original code. It is `skyLight >= 15`, so walking under a single leaf flips it and stops
  the music. This is the bug the current check fixes.
- **Sampling a ring around the player** reads the walls of a hole or the terrain of a slope
  and reports a roof that is not overhead: a freshly dug 1x1 shaft reads as a cave because
  its own walls count.
- **Sampling several blocks upward** escapes through a thin roof and reports open sky.
- **Testing whether the block above collides** fails because leaves have a collision box and
  therefore count as a roof.

Two things to preserve when touching this code:

- The debug log runs on **every client tick**, so it must only print on a result *flip*.
  Tick rates can be pushed past 20/s (Carpet's `tick sprint` reaches thousands) and an
  unconditional line grows the log by gigabytes while idling.
- The `lastLoggedUnderground == null` check must come **before** the comparison. While debug
  mode is off the comparison never runs, so the field stays null and unboxes to an NPE the
  moment debug mode is switched on. This has already crashed a client once.

## Debugging in game

`/reactivemusic marker <text>` writes a marker to the log together with the current
underground state, so a test run can be annotated and read back without waiting for a flip:

```
[ReactiveMusic] MARKER: under a leaf | using=false singlePoint=true sampled=false
headSky=14 y=-60.00 surfaceHeight=-57 pos=52,-61,54 col[13,14,15,15] ring[14,13,...]
```

- `using` is the decision in force; `singlePoint` and `sampled` are the two candidate checks
  side by side, so a bad decision shows which one disagreed.
- `headSky` is what the check reads.
- `col` / `ring` are raw sky light around the player; purely diagnostic.

Debug output goes to `latest.log` only. `/reactivemusic toggleLogging` additionally mirrors
it into the chat.
