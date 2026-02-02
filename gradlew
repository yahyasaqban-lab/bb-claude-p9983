#!/usr/bin/env bash

# Minimal Gradle wrapper bootstrap
set -euo pipefail

DIR="$(cd "$(dirname "$0")" && pwd)"

WRAPPER_JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"
PROPS_FILE="$DIR/gradle/wrapper/gradle-wrapper.properties"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Missing gradle-wrapper.jar" >&2
  exit 1
fi

JAVA_CMD="${JAVA_HOME:+$JAVA_HOME/bin/}java"

exec "$JAVA_CMD" -Xmx1g -Dorg.gradle.appname=gradlew -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
