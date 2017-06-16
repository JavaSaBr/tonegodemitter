# Toneg0d.Emitter 2.1.1 #

It's a fork of toneg0d.emitter to integrate it to jME3-SpaceShift-Editor.

## How to use

#### Gradle


```
#!groovy

allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.JavaSaBr:tonegodemitter:2.1.1'
}
```

    
#### Maven

```
#!xml


<repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependency>
        <groupId>com.github.JavaSaBr</groupId>
        <artifactId>tonegodemitter</artifactId>
        <version>2.1.1</version>
    </dependency>
```