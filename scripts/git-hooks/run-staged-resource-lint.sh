#!/usr/bin/env sh
# Run lint only for Gradle modules whose staged values resources changed.
set -eu

repo_root=$(git rev-parse --show-toplevel)
cd "$repo_root"

# Android Studio's default macOS SDK location is available even when a shell
# session has not exported ANDROID_HOME. Do not read local.properties.
if [ -z "${ANDROID_HOME:-}" ] && [ -d "$HOME/Library/Android/sdk" ]; then
    export ANDROID_HOME="$HOME/Library/Android/sdk"
fi

modules=$(
    git diff --cached --name-only --diff-filter=ACMR |
        awk '
            match($0, /\/src\/main\/res\/values[^\/]*\/[^\/]+\.xml$/) {
                module_path = substr($0, 1, RSTART - 1)
                gsub("/", ":", module_path)
                print ":" module_path
            }
        ' |
        sort -u
)

if [ -z "$modules" ]; then
    exit 0
fi

printf '%s\n' 'GithubSearch: staged values resources detected; running affected module lint.'
for module in $modules; do
    ./gradlew "$module:lintDebug"
done
