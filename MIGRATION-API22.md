# RVX patches: ReVanced Patcher API v21 -> v22

Phase 1 of the migration (spoof / client work is NOT part of this drop).
This tree was converted **without being able to compile it** (no Kotlin compiler, Gradle
or patcher v22 library were available). Expect a first round of compile errors; send them back
and they will be fixed iteratively.

## What changed

### Build
* `settings.gradle.kts`: plugin `app.revanced.patches` `1.0.0-dev.10`, `settings { extensions { ... } }`,
  protobuf JitPack workaround (same as the official ReVanced repo). Credentials still come from
  `gpr.user` / `gpr.key` or `GITHUB_ACTOR` / `GITHUB_TOKEN`.
* `gradle/libs.versions.toml`: `revanced-patcher 22.0.0`, `smali 3.0.8`, `agp 8.2.2` (plugin alias has no version),
  `protobuf master-SNAPSHOT`, `guava` added.
* Gradle wrapper, `gradlew`, `gradlew.bat` copied from the official v22 repo (Gradle 9.3.1).
* `patches/build.gradle.kts`: `-Xcontext-parameters` instead of `-Xcontext-receivers`; JsonGenerator
  (`app/revanced/generator`, `generatePatchesFiles`) removed. `patches/api/` (stale binary-compat dump) removed:
  run `./gradlew :patches:apiDump` once the build works.
* `.github/workflows/release.yml` no longer calls `generatePatchesFiles`.

### Extensions
* `extensions/shared` -> **`extensions/rvx-shared`** (`extendWith("extensions/rvx-shared.rve")`,
  `:extensions:rvx-shared:stub`).
* `extension { name = ... }` no longer exists; the `.rve` name is derived from the module path, so
  `spoof-wifi` and `spoof-signature` were moved to
  `extensions/all/connectivity/wifi/spoof/spoof-wifi` and `extensions/all/misc/signature/spoof-signature`
  (the `extendWith` strings are unchanged).
* Global `extensions/proguard-rules.pro` added (common rules); module specific rules stay in each module.
* Java package is still `app.revanced.extension.*` (see "Known limitations").

### Patches
* Patch names get the suffix ` (rvx)` (`PatchList.patchName`, `RVX_PATCH_SUFFIX`).
  `PatchList.title` is intentionally unchanged: it is used to derive the extension patch-status method names.
* `execute {}` -> `apply {}`, `finalize {}` -> `afterDependents {}` (labels `execute@` / `finalize@` keep the old `return@` working).
* Imports moved to `app.revanced.patcher.extensions.*` and `app.revanced.com.android.tools.smali.dexlib2.mutable.*`;
  `MutableClass` -> `MutableClassDef`; `Patch<*>` -> `Patch`; `PackageName` / `VersionName` aliases -> `String`.
* Options: `key` removed, `title` -> `name`. Options of other patches that were read by key are now exported
  through file level variables (`youtubeCustomBrandingAppIconOption`, `musicCustomBrandingAppIconOption`,
  `overlayButtonsIconTypeOption`).
* `ShortcutPatch`: option keys replaced by explicit shortcut ids.
* Fingerprints: `legacyFingerprint(...)` keeps its call surface, but the engine
  (`util/fingerprint/LegacyFingerprint.kt`) was re-implemented on top of `classDefs`, `firstMethod`, `firstClassDef`.
  Matching semantics are the same as the v21 `Fingerprint`. `Fingerprint` / `Match` -> `LegacyFingerprint` / `LegacyMatch`.
* `classes` / `proxy(...).mutableClass` / `classBy` -> `classDefs` / `firstClassDef` / `firstMethod`
  (matches are collected first and mutated afterwards).

## Assumptions that could not be verified
1. `firstMethod(method: Method)` and `firstClassDef(type)` return the mutable instances (same use as in the official `BytecodeUtils`).
2. Module path -> `.rve` name mapping of the v22 plugin.
3. Option objects created inside the patch builder can be handed to other patches through a variable (the official repo does this for `gmsCoreVendorGroupIdOption`).

## Known limitations
* Bundle collisions: only the resource name (`rvx-shared.rve`) was made unique. The classes inside are still in
  `app.revanced.extension.*`, so applying the official ReVanced bundle and this bundle to the same app can still
  produce duplicate classes in the dex.
* README / patches.json / .releaserc were not regenerated.
* Patch names changed: CLI / manager configs that select patches by name must use the new names.
