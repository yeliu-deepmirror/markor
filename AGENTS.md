Mandatory rules for AI coding agents contributing to this repo. Direct user instructions win — flag the conflict when they do.

## What this repo is

Markor is an Android text editor (Java, AndroidX) for notes and to-dos, supporting Markdown, todo.txt, Zim/WikiText, AsciiDoc, CSV, Org-Mode, and more. Fully offline, no NDK, one APK for all architectures.

## Repository layout

```
├── app/src/main/java/net/gsantner/
│   ├── markor/                # App code
│   │   ├── format/             # Per-format packages: markdown, todotxt, wikitext, asciidoc, csv, keyvalue, orgmode, plaintext, general, binary
│   │   ├── frontend/           # File browser, text/file search, textview helpers
│   │   ├── activity/           # Activities, incl. activity/openeditor
│   │   ├── model/, util/, web/, widget/
│   └── opoc/                  # Reusable "one-person-open-source" library code (frontend/base, settings, textview, util, web, wrapper) — kept generic, not Markor-specific
├── app/src/main/res/           # Android resources; values-<lang> per locale (translations, managed via Crowdin)
├── app/src/main/assets/        # highlight.js themes/languages, templates
├── app/src/test/java/          # JUnit tests, mirrors app/src/main/java/net/gsantner package layout
├── app/src/flavorAtest/        # Build flavor: F-Droid/test flavor resources
├── app/src/flavorGplay/        # Build flavor: Google Play flavor resources
├── app/thirdparty/            # Vendored third-party sources/assets
├── doc/                        # Per-feature writeups (CSV, Syncthing, line numbers, etc.) and doc/keyboard-shortcuts.md, doc/maintain.md
├── samples/                    # Example documents per format (Markdown, Zim, todo.txt, Org-mode, CSV, taskpaper) used for manual testing and in-app templates
├── metadata/                   # F-Droid/store listing: descriptions, changelogs, screenshots (en-US authoritative, zz = source for translation)
├── experiments/                # Unmerged/exploratory patches, not part of the build
├── build.gradle, app/build.gradle, settings.gradle, gradle.properties, gradlew  # Gradle build
├── Makefile                    # make targets: build, test, lint, deptree, spellcheck, install, run, clean
├── .github/workflows/build-android-project.yml  # CI: make clean all
├── crowdin.yml                 # Translation sync config (crowdin.com/project/markor)
├── CHANGELOG.md, NEWS.md       # NEWS.md is curated per-release prose; CHANGELOG.md is the generated commit list
├── CONTRIBUTORS.md
└── README.md                   # Features, FAQ, contribute/develop guide, privacy, license
```

## 1. Read the docs before starting a task

| Doc | Covers |
|---|---|
| root `README.md` | Features, FAQ (file browser, Markdown, todo.txt), Contribute/Develop section, technologies used, privacy, license |
| `doc/maintain.md` | How release notes are generated (`git logshort` command for CHANGELOG.md) |
| `doc/keyboard-shortcuts.md` | Editor keyboard shortcuts |
| `doc/README.md` | Index of per-feature docs under `doc/` |
| `doc/2023-06-02-csv-readme.md` | CSV format implementation notes |
| `Makefile` | Local build/test/lint/install/run targets and what each does |
| `CONTRIBUTORS.md` | Contributor attribution conventions |

## 2. Update docs before opening a PR

Ship the doc fix with the code — not as a follow-up. Change types and their targets:

* A new or changed document format (parser, syntax highlighter, or view-mode renderer) under `app/src/main/java/net/gsantner/markor/format/` → add or update a doc under `doc/` if the format has one, and update the format list in the "Features" section of `README.md`.
* A new sample document demonstrating a format → add it under `samples/` and, if it's referenced from an in-app "new file from template" dialog, confirm the template list in `README.md` still matches.
* User-facing feature or UI change → note it in `NEWS.md` under the current unreleased version heading; `CHANGELOG.md` is regenerated from git log per `doc/maintain.md`, don't hand-edit it.
* Build/CI change (`Makefile`, `.github/workflows/build-android-project.yml`, `build.gradle`) → update the "Develop" / "Technologies" section of `README.md` if the described workflow changed.
* New translatable string → add it only to the default `app/src/main/res/values/strings.xml`; per-locale `values-<lang>/` files are managed via Crowdin sync, not hand-edited.

## 3. Build and tests must pass before opening a PR

CI (`.github/workflows/build-android-project.yml`) runs `make clean all`, which chains `spellcheck lint deptree test build aapt_dump_badging`. Before opening a PR:

* Run `make test` (unit tests under `app/src/test/java/`) and `make lint` locally; both must be clean, or make sure a new lint warning is justified.
* If you touched `app/src/main/res/values/strings.xml`, run `make spellcheck` (requires `ispell`) and check `dist/lint/stringsxml-spellcheck.txt` for anything you introduced.
* For editor/format changes, prefer adding a JUnit test under `app/src/test/java/net/gsantner/markor/format/<format>/` alongside the existing pattern/highlighter tests rather than relying on manual APK testing alone.
* Full local build: `make all` (requires `ANDROID_SDK_ROOT` set); outputs land under `dist/`.

## 4. Code style

Follow the AOSP Java code style (per `README.md`'s Develop section). Use Android Studio's *Reformat Code* before committing. `net.gsantner.opoc` is meant to stay generic/reusable — don't add Markor-specific logic there; put it under `net.gsantner.markor` instead.

## 5. Keep prose and comments concise

* Docs: tight prose. One sentence beats two. Cut hedges, cut narration, cut sentences that restate a heading.
* Code comments: add one only when a reader can't derive the why from the identifiers and structure. Skip comments that restate what the code does, narrate the task, or reference the PR / caller. One short line, not a docstring paragraph.

## 6. Destructive-action discipline

Follow the harness's default git-safety protocol (no force-push, git reset --hard, branch deletion, or --no-verify without explicit user confirmation this session). One repo-specific addition:

* Don't hand-edit generated files: `CHANGELOG.md` (regenerated from git log, see `doc/maintain.md`) and per-locale `app/src/main/res/values-<lang>/strings.xml` (synced from Crowdin). Edit the source instead (commit messages / `NEWS.md`, `values/strings.xml`).

## 7. Scope discipline

* Broad reshuffles ship separately. A drive-by within the files you're already touching is fine; a sweep across unrelated packages is its own PR.
* No backwards-compatibility shims or feature flags for hypothetical callers.
* Keep the codebase clean as you go. When you spot duplicated logic — same conversion, same boilerplate block — hoist it into a shared helper in the same PR if the lift is small. Refactor when it makes the diff smaller and cleaner; don't refactor for its own sake. The boundary: if the cleanup touches only the files you're already changing plus a new helper file, it's in scope; if it would ripple across unrelated packages, ship separately.

## 8. Prefer the simplest design that solves the concrete problem

Pick the design that solves only the requirement on the table. Don't add abstraction layers, configurable knobs, extra primitives, or extension points "in case we need them later." If a future requirement materializes, the design can grow then — when its actual constraints are known, not guessed.

When proposing or reviewing a design, drop the bullet that starts with "this also lets us…" or "leaves room for…". Two-tier mechanisms, pluggable backends, and speculative interfaces are debt: they widen the API surface, multiply test cases, and lock in assumptions that may turn out wrong. Pairs with §7 (no scope creep within a PR) and §5 (no narrative comments about hypothetical callers).

## 9. Name things fully; avoid abbreviations

Prefer the unabbreviated English word for packages, directories, modules, types, and struct / field names. Keep the short form only when it's already standard (src, docs, api, id, unit suffixes like _ns / _ms, well-known initialisms like xml / json / url, or names that must match an existing convention like `net.gsantner.opoc`/`Gs`-prefixed classes).

When in doubt, spell it out — a name that makes sense on first read beats one that saves three characters.
