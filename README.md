# Toneg0d.Emitter 3.0.2 #

It's a fork of toneg0d.emitter.

## How to use

The former Bintray repository is no longer available. Version **3.0.2** is
available from JitPack using the coordinates below.

The verified runtime combination is **jMonkeyEngine 3.2.4-stable and Java 8**.
The library's POM references `jme3-core` 3.2.0-stable, which is unavailable from
Maven Central. The examples explicitly select 3.2.4-stable; use that version
for your application's other jME dependencies, including desktop/renderer.
If your build tool runs on a newer JDK, compile the application for Java 8 and
run it on a Java 8 runtime. Newer runtime combinations need separate testing.

#### Gradle

```groovy
repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.JavaSaBr")
        }
    }
}

dependencies {
    implementation("com.github.JavaSaBr:tonegodemitter:3.0.2")
    implementation("org.jmonkeyengine:jme3-core:3.2.4-stable")
}
```

#### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.JavaSaBr</groupId>
    <artifactId>tonegodemitter</artifactId>
    <version>3.0.2</version>
</dependency>

<dependency>
    <groupId>org.jmonkeyengine</groupId>
    <artifactId>jme3-core</artifactId>
    <version>3.2.4-stable</version>
</dependency>
```

Maven's default JAR dependency type is intentional: the old `pom` type did not
request the library JAR. In modern Gradle, use `implementation` instead of the
removed `compile` configuration.
