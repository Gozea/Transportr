Transportr | [![Build and test](https://github.com/grote/Transportr/actions/workflows/build.yml/badge.svg)](https://github.com/grote/Transportr/actions/workflows/build.yml)
============================================================================================================================================================================

The public transport companion that respects your privacy and your freedom.
Transportr is a non-profit app developed by people around the world to make using public transport as easy as possible wherever you are. 

[![Transportr Logo](./app/src/main/res/mipmap-xhdpi/ic_launcher.png)](https://transportr.app)

Please **[visit the website](https://transportr.app)** for more information!

If you find any issues with this app, please report them at [the issue tracker](https://github.com/grote/Transportr/issues). Contributions are both encouraged and appreciated. If you like to contribute please [check the website](https://transportr.app/contribute) for more information.

[![Follow @TransportrApp](artwork/twitter.png)](https://twitter.com/TransportrApp)

Get Transportr
--------------

<a href="https://f-droid.org/packages/de.grobox.liberario">
    <img src="./artwork/f-droid.png"
    alt="Get it on F-Droid"
    height="80">
</a>
<a href="https://play.google.com/store/apps/details?id=de.grobox.liberario">
    <img src="./artwork/google-play.png"
    alt="Get it on Google Play"
    height="80"/>
</a>

Pre-releases and beta versions for advanced users are available via [a special F-Droid repository](http://grobox.de/fdroid/).

Screenshots
-----------
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1_FirstStart.png" width="290">](fastlane/metadata/android/en-US/images/phoneScreenshots/1_FirstStart.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2_SavedSearches.png" width="290">](fastlane/metadata/android/en-US/images/phoneScreenshots/2_SavedSearches.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/3_Trips.png" width="290">](fastlane/metadata/android/en-US/images/phoneScreenshots/3_Trips.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/4_TripDetails.png" width="290">](fastlane/metadata/android/en-US/images/phoneScreenshots/4_TripDetails.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/5_Station.png" width="290">](fastlane/metadata/android/en-US/images/phoneScreenshots/5_Station.png)
[<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/6_Departures.png" width="290">](fastlane/metadata/android/en-US/images/phoneScreenshots/6_Departures.png)


Building From Source
--------------------

If you want to start working on Transportr and if you haven't done already, you should [familiarize yourself with Android development](https://developer.android.com/training/basics/firstapp/index.html) and [set up a development environment](https://developer.android.com/sdk/index.html).

The next step is to clone the source code repository.

    $ git clone https://github.com/grote/Transportr.git

### Requirements

To build Transportr you need:

* The latest stable **Android Studio** (which bundles a suitable JDK).
* The **Android SDK Platform 36** and **Build-Tools 36.0.0** (Android Studio offers to install these while syncing).
* A **JDK 17 or newer** for command-line builds. Any current LTS JDK (17, 21, 25, ...) works, because the build produces Java 17 bytecode regardless of the JDK that runs Gradle.

The project uses the Gradle wrapper, so no separate Gradle installation is needed.
The repository pins the Gradle wrapper and all dependencies (Gradle's built-in
[dependency verification](https://docs.gradle.org/current/userguide/dependency_verification.html)
replaces the old `witness` plugin), so a clone always builds against known-good artifacts.

### Android Studio

Open the root directory (`File > Open...`) and let Android Studio sync. The
checked-in `.idea/gradle.xml` sets the Gradle JDK to `GRADLE_LOCAL_JAVA_HOME`,
so Android Studio uses the JDK it already ships with instead of asking for a
machine-specific path. `local.properties` (which points at your SDK) is created
automatically and is not committed.

### Command Line

If you don't want to use an IDE like Android Studio, you can build Transportr on the command line as follows.

    $ cd Transportr
    $ ./gradlew assembleDebug

This produces `app/build/outputs/apk/debug/app-debug.apk`.

If Gradle cannot find your Android SDK, either create a `local.properties` file
containing `sdk.dir=/path/to/Android/Sdk` or set the `ANDROID_HOME` environment
variable. Note that `assembleRelease` needs signing configuration (see the
comments in `app/build.gradle`) and produces an unsigned APK otherwise.

License
-------

[![GNU GPLv3 Image](https://www.gnu.org/graphics/gplv3-127x51.png)](https://www.gnu.org/licenses/gpl-3.0.html)

This program is Free Software: You can use, study share and improve it at your
will. Specifically you can redistribute and/or modify it under the terms of the
[GNU General Public License](https://www.gnu.org/licenses/gpl.html) as
published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.


Acknowledgements
----------------

<a href="https://www.jawg.io"><img src="./artwork/jawgmaps.png" height="58"/></a>

[JawgMaps](https://www.jawg.io) is a provider of online custom maps, geocoding and routing based on OpenStreetMap data. We would like to thank them for providing their vector map tile service to Transportr free of charge.
