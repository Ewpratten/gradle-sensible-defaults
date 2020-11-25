# Sensible Defaults for Gradle

[`gradle-sensible-defaults`](https://plugins.gradle.org/plugin/ca.retrylife.gradle.sensibledefaults) is a [Gradle](https://gradle.org/) plugin I have built for personal use. The goal of this plugin is to automatically configure buildsystem settings, and reduce the amount of copy+paste I need to do when setting up a new project.

## Installation

### Using the Plugins DSL

Add the following to the top of your `build.gradle` file`:

```groovy
plugins {
    id "ca.retrylife.gradle.sensibledefaults" version "1.+"
}
```

### Using the legacy plugin system

Add the following to the top of your `build.gradle` file`:

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.ca.retrylife.gradle.sensibledefaults:gradle-sensible-defaults:1.+"
  }
}

apply plugin: "ca.retrylife.gradle.sensibledefaults"
```
