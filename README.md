# Skript-Three
**Skript-Three** is a Minecraft plugin for Paper, which allows server owners and other people
to modify their servers without learning Java. It can also be useful if you
*do* know Java; some tasks are quicker to do with Skript, and so it can be used
for prototyping etc.

This Github fork of Skript is based on [**SkriptLang's**](https://github.com/SkriptLang/Skript) improvements which was built
on Njol's original Skript.

To help differentiate between SkriptLang's Skript, we'll be referring to this fork as '**Skript-Three**', but internally it is still called 'Skript'.

## Requirements
- Java 21+
- PaperMC server.    
You heard it right, **CraftBukkit/Spigot** does *not* work.

Skript supports only the **latest** patch versions of Minecraft 1.20+.
For example, this means that 1.20.6 is supported, but 1.20.1 is *not*.
Testing with all old patch versions is not feasible for us.

## Download
You can find the downloads for each version with their release notes in the [releases page](https://github.com/SkriptDev/Skript/releases).

## Documentation
Documentation is available [**HERE**](https://skriptdev.github.io/docs/Skript/latest) for the
latest version of Skript.

## Reporting Issues
Please see our [contribution guidelines](https://github.com/SkriptDev/Skript/blob/master/.github/contributing.md)
before reporting issues.

[//]: # (## Help Us Test)

[//]: # (Wanting to help test Skript's new features and releases?)

[//]: # (You can head on over to our [Official Testing Discord]&#40;https://discord.gg/ZPsZAg6ygu&#41;, and whenever we start testing new features/releases you will be the first to know.)

[//]: # ()
[//]: # (Please note this is not a help Discord.)

[//]: # (If you require assistance with how to use Skript please check out the [Relevant Links]&#40;https://github.com/SkriptLang/Skript#relevant-links&#41; section for a list of available resources to assist you.)

## A Note About Add-ons
We don't support add-ons here, even though some of Skript developers have also
developed their own add-ons.

## Compiling
Skript uses Gradle for compilation. Use your command prompt of preference and
navigate to Skript's source directory. Then you can just call Gradle to compile
and package Skript for you:

```bash
./gradlew clean build # on UNIX-based systems (mac, linux)
gradlew clean build # on Windows
```

You can get source code from the [releases page](https://github.com/SkriptDev/Skript/releases).
You may also clone this repository, but that code may or may not be stable.

### Testing
Skript has some tests written in Skript. Running them requires a Minecraft
server, but our build script will create one for you. Running the tests is easy:

```
./gradlew (quickTest|skriptTest|skriptTestJava21)
```

<code>quickTest</code> runs the test suite on newest supported server version.
<code>skriptTestJava21</code> (1.20.6+) runs the tests on Java 21 supported versions.
<code>skriptTest</code> runs the tests on all versions.
That is, it runs skriptTestJava11, skriptTestJava17, and skriptTestJava21.

By running the tests, you agree to Mojang's End User License Agreement.

### Importing to Eclipse
With new Eclipse versions, there is integrated Gradle support, and it actually works now.
So, first get latest Eclipse, then import Skript as any Gradle project. Just
make sure to **keep** the configuration when the importer asks for that!

If you encounter strange issues, make sure you follow the instructions above and have
actually downloaded latest Eclipse or update your installation correctly. Skript's
new Gradle version (starting from dev26) does not work very well with older Eclipse
versions. Also, do *not* use Gradle STS; it is outdated.

### Importing to IDEA
You'll need to make sure that nullness annotations are working correctly. Also,
when sending pull requests, make sure not to change IDEA configuration files
that may have been stored in the repository.

### Releasing
```
./gradlew clean build
./gradlew <flavor>Release
```
Available flavors are github and spigot. Please do not abuse flavors by
compiling your own test builds as releases.

## Contributing
Please review our [contribution guidelines](https://github.com/SkriptDev/Skript/blob/master/.github/contributing.md).
In addition to that, if you are contributing Java code, check our
[coding conventions](https://github.com/SkriptDev/Skript/blob/master/code-conventions.md).

## Maven Repository

[//]: # (TODO)
> [!WARNING]
> This needs to be updated

If you use Skript as (soft) dependency for your plugin, and use maven or Gradle,
this is for you.

First, you need to add the Maven repository at the **END** of all your repositories. Skript is not available in Maven Central.
```gradle
repositories {
    maven {
        url 'https://repo.skriptlang.org/releases'
    }
}
```

Or, if you use Maven:
```maven
<repositories>
    <repository>
        <id>skript-releases</id>
        <name>Skript Repository</name>
        <url>https://repo.skriptlang.org/releases</url>
    </repository>
</repositories>
```

For versions of Skript after dev37 you might need to add the paper-api repository to prevent build issues.

```gradle
maven {
    url 'https://repo.destroystokyo.com/repository/maven-public/'
}
```

Or, if you use Maven:
```maven
<repository>
    <id>destroystokyo-repo</id>
    <url>https://repo.destroystokyo.com/content/repositories/snapshots/</url>
</repository>
```

Then you will also need to add Skript as a dependency.
```gradle
dependencies {
    implementation 'com.github.SkriptLang:Skript:[versionTag]'
}
```

An example of the version tag would be ```2.8.5```.

> Note: If Gradle isn't able to resolve Skript's dependencies, just [disable the resolution of transitive dependencies](https://docs.gradle.org/current/userguide/resolution_rules.html#sec:disabling_resolution_transitive_dependencies) for Skript in your project.

Or, if you use Maven:
```
<dependency>
    <groupId>com.github.SkriptLang</groupId>
    <artifactId>Skript</artifactId>
    <version>[versionTag]</version>
    <scope>provided</scope>
</dependency>
```

## Relevant Links
* [Skript Hub](https://skripthub.net)
* [SkriptLang's Skript](https://github.com/SkriptLang/Skript)
* [Original Skript at Bukkit](https://dev.bukkit.org/bukkit-plugins/skript) (inactive)

Note that these resources are not maintained by Skript's developers. Don't
contact us about any problems you might have with them.

## Developers
You can find all contributors [here](https://github.com/SkriptDev/Skript/graphs/contributors).

All code is owned by its writer, licensed for others under GPLv3 (see LICENSE)
unless otherwise specified.
