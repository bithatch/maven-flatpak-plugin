# Flatpak Maven Plugin

Generate a Flatpak manifest and other required files from a Maven POM to create a distributable application. It will turn your Java application into a Flatpak package, which can easily be installed and launched by Linux users of any distribution that supports Flatpak.

 * Generates a directory of files from which `flatpak-builder` can be run to build a package.
 * Either copies dependencies, or links to their original locations on Maven Central (in which case the package build downloads them). This makes for a small packages.
 * Generates the Flatpak manfiest using a the simple build system.
 * Generates AppStream metadata from POM metadata and plugin configuration.
 * Generates Desktop Entry from POM metadata and plugin configuration.
 * Copies icons, screenshots and thumbnails to package data.
 * Generates launcher script.
 * Detects version and adds Java Flatpak SDK extension.
 * Runs `flatpak-builder` on generated directory to create and install a Flatpak for testing.
 
It is *not* a Flatpak Build System, although this may be a first step towards that.

*Note that the name of this Github no longer matches the plugin artifact ID, `flatpak-maven-plugin`.*

## Maven Goals

Two goals are currently provided. 

 * `generate`. Generates Flatpak Manifest and other data.
 * `build`. Builds and optionally install the Flatpak for testing.

## Getting Started.

### Add Plugin To Your POM

This plugin is available in Maven Central shortly. 

```xml
<plugin>
	<groupId>uk.co.bithatch</groupId>
	<artifactId>flatpak-maven-plugin</artifactId>
	<version>0.0.1</version>
	<configuration>
		<!-- TODO add configuration -->
	</configuration>
</plugin>
```

### Configure Your POM

The plugin takes as much information as it can from *other* standard POM elements, so it is important that you fill in as many of these as possible. This includes ..

 * `<artifactId>` and `<groupId>`. A POM requires these anyway, they are used to derive the full namespace of the App Id. 
 * `<name/>`.  Required, used for application meta-data and desktop entry.
 * `<description/>`.  Required, used for application meta-data and desktop entry.
 * `<url/>`. Required. Used for application meta-data.
 * `<licenses/>`. Required. Used for application meta-data.
 * `<scm/>`. Optional. Used for application meta-data.
 * `<issueManagement/>`. Optional. Used for application meta-data.
 * `<developers/>`. Optional. Used for application meta-data.
 
### Add Resources

The plugin expects resources to exist in a standard location (the locations of which can be overridden by configuration).

 * `src/flatpak/icons` should contain a single file, preferably named `icon` with any supported image extension (`.svg` is recommended).
 * `src/flatpak/screenshots` should contain a single file, preferable named `screenshot`  with any supported image extension (`.jpg` is recommended).
 * `src/flatpak/thumbnails` should contain at least one file, preferable named `thumbnail`  with any supported image extension (`.jpg` is recommended).
 
### Configure The Plugin

For anything else that cannot be determined in any other way, you must provide plugin configuration. The bare minimum for this is the `<mainClass/>`.

```xml
<configuration>
	<mainClass>com.acme.Abc</mainClass>
</configuration>
```

You *very probably* want to add your own portals too. Do this by overriding the `<manifest/>` and the `<finishArgs/>` within.

```xml
<configuration>
	...
	<manifest>
		<finishArgs>
			<finishArg>--socket=x11</finishArg>
			<finishArg>--filesystem=home</finishArg>
		</finishArgs>
	</manifest>
</configuration>
```

You *probably* want to supply your own `appId` if the automatically derived one does not suit your needs.

```xml
<configuration>
	<manifest>
		<appId>com.acme.Abc</appId>
	</manifest>
</configuration>
```

A more complete example ..

```xml
<configuration>
	<mainClass>com.acme.Abc</mainClass> <!-- required -->
	<modules>false</modules> <!-- disable JPMS -->
	<manifest>
		<appId>com.acme.Abc</appId>
		<sdkExtensions>
			<sdkExtension>org.freedesktop.Sdk.Extension.openjdk17</sdkExtension>
		</sdkExtensions>
		<finishArgs>
			<finishArg>--socket=session-bus</finishArg>
			<finishArg>--socket=x11</finishArg>
			<finishArg>--socket=ssh-auth</finishArg>
			<finishArg>--device=dri</finishArg>
			<finishArg>--share=network</finishArg>
			<finishArg>--share=ipc</finishArg>
			<finishArg>--filesystem=home</finishArg>
		</finishArgs>
	</manifest>
	<launcherPreCommands>
		<launcherPreCommand>echo '(C) 2023 AwesomeSuperSofwareSolutions Ltd'</launcherPreCommand>
	</launcherPreCommands>
	<excludeArtifacts>
		<!-- not needed by flatpak version, so exclude it -->
		<excludeArtifact>com.install4j</excludeArtifact>
	</excludeArtifacts>
	<iconFile>src/main/svg/icon.svg</iconFile> <!-- icon not in standard location -->
</configuration>
```

Any element in either the `<manifest/>`, `<desktopEntry/>` or `<metaInfo/>` can generally be overridden. There are also lots of other options in the root `<configuration/>`.  

## Usage

```
mvn clean package uk.co.bithatch:maven-flatpak-plugin:generate
```

This will by default generate the Flatpak manifest and others in `target/app`. So from here you can build the package.

```
cd target/app
flatpak-builder build-dir com.acme.Abc.yml
```

And then test and run.

```
flatpak-builder --user --install --force-clean build-dir com.acme.Abc.yml
flatpak run com.acme.Abc
```

Find out more in the [official documentation](https://docs.flatpak.org/en/latest/building.html).



### Bind The Plugin To A Phase

You might want to `generate` the Flatpak data as part of the standard Maven `package` phase.

```xml
<executions>
	<execution>
		<id>myapp-flatpak</id>
		<phase>package</phase>
		<goals>
			<goal>generate</goal>
		</goals>
	</execution>
</executions>
```

In which case you can just do `mvn clean package`. 

## TODO

There is a lot still to do, some highlights include.

 * Auto-detect main class.
 * Publish to Flathub.
 * Investigate Flatpak build systems.
 * More app types.
 


