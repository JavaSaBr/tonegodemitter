# Toneg0d.Emitter 3.0.2 #

It's a fork of toneg0d.emitter.

## How to use

#### Gradle

```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/javasabr/maven" 
    }
}

dependencies {
    compile 'toneg0d:emitter:3.0.2'
}
```

#### Maven

```xml
<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>bintray-javasabr-maven</id>
        <name>bintray</name>
        <url>https://dl.bintray.com/javasabr/maven</url>
    </repository>
</repositories>

<dependency>
    <groupId>toneg0d</groupId>
    <artifactId>emitter</artifactId>
    <version>3.0.2</version>
    <type>pom</type>
</dependency>
```