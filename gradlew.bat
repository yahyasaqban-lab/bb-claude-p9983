@echo off
set DIR=%~dp0
set WRAPPER_JAR=%DIR%gradle\wrapper\gradle-wrapper.jar
if not exist "%WRAPPER_JAR%" (
  echo Missing gradle-wrapper.jar
  exit /b 1
)
set JAVA_CMD=java
if defined JAVA_HOME set JAVA_CMD=%JAVA_HOME%\bin\java
"%JAVA_CMD%" -Xmx1g -Dorg.gradle.appname=gradlew -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
