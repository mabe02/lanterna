## Welcome ##
![http://wiki.lanterna.googlecode.com/git/images/screenshots/screenshot1.png](http://wiki.lanterna.googlecode.com/git/images/screenshots/screenshot1.png)

Lanterna is a Java library allowing you to write easy semi-graphical user interfaces in a text-only environment, very similar to the C library [curses](http://en.wikipedia.org/wiki/Curses_(programming_library)) but with more functionality. Lanterna is supporting xterm compatible terminals and terminal emulators such as konsole, gnome-terminal, putty, xterm and many more. One of the main benefits of lanterna is that it's not dependent on any native library but runs 100% in pure Java.

Also, when running Lanterna on computers with a graphical environment (such as Windows or Xorg), a bundled terminal emulator written in Swing will be used rather than standard output. This way, you can develop as usual from your IDE (most of them doesn't support ANSI control characters in their output window) and then deploy to your headless server without changing any code.

Lanterna is structured into three layers, each built on top of the other and you can easily choose which one fits your needs best.
  1. The first is a low level terminal interface which gives you the most basic control of the terminal text area. You can move around the cursor and enable special modifiers for characters put to the screen. You will find these classes in package `com.googlecode.lanterna.terminal`.
  1. The second level is a full screen buffer, the whole text screen in memory and allowing you to write to this before flushing the changes to the actual terminal. This makes writing to the terminal screen similar to modifying a bitmap. You will find these classes in package `com.googlecode.lanterna.screen`.
  1. The third level is a full GUI toolkit with windows, buttons, labels and some other components. It's using a _very_ simple window management system (basically all windows are modal) that is quick and easy to use. You will find these classes in package `com.googlecode.lanterna.gui`.

## Maven ##
Lanterna is available on [Maven](http://maven.apache.org) [Central](http://search.maven.org), through [Sonatype OSS hosting](http://oss.sonatype.org). Here's what you want to use:
```
    <dependency>
        <groupId>com.googlecode.lanterna</groupId>
        <artifactId>lanterna</artifactId>
        <version>2.1.9</version>
    </dependency>
```
There's also a preview release available of the upcoming 3.0 release:
```
    <dependency>
        <groupId>com.googlecode.lanterna</groupId>
        <artifactId>lanterna</artifactId>
        <version>3.0.0-beta1</version>
    </dependency>
```

## News ##
**2015-06-28**
  * Lanterna 3.0.0-beta1 has been released and pushed to Sonatype
    * Lots of various bugfixes
    * Several dialog classes available
    * Threading model updated
    * `GridLayout` (heavily inspired from SWT) now properly implemented
  * This is the last release published on Google Code, see you on ~~SourceForge~~ GitHub!

**2015-02-23**
  * Lanterna 3.0.0-alpha6 has been released and pushed to Sonatype
    * Fixes the Windows regression in alpha 5
    * GUI window system refactoring
    * Various submitted bugfixes

**2015-01-20**
  * Lanterna 3.0.0-alpha5 has been released and pushed to Sonatype
  * Mostly work on the GUI system and various bugfixes but also
    * Text color API in `Terminal` has changed again, hopefully for the last time
    * `ScreenBuffer` class is now public and exposed in the screen layer
    * New interface `TextImage`, drawable by `TextGraphics`

**2014-10-16**
  * Lanterna 3.0.0-alpha4 has been released and pushed to Sonatype
  * Lots and lots of changes, highlights include
    * Themed text graphics
    * New `VirtualScreen` implementation of `Screen`
    * `ACS` class is now called `Symbols`
    * New GUI system is becoming more useable

**2014-09-22**
  * Lanterna 2.1.9 has been released and pushed to Sonatype
  * The changelog is available [here](https://code.google.com/p/lanterna/wiki/ChangesFrom218to219)

**2014-07-24**
  * Lanterna 3.0.0-alpha3 has been released and pushed to Sonatype.
    * `TerminalPosition`, `TerminalSize`, `SGR`, `ACS` and `TextColor` has moved to `com.googlecode.lanterna` package
    * A couple of nasty regressions fixed

**2014-07-18**
  * Lanterna 3.0.0-alpha2 has been released and pushed to Sonatype. Some API changes but the more interesting part is the new class [TextGraphics](https://wiki.lanterna.googlecode.com/git/apidocs/3.0/com/googlecode/lanterna/graphics/TextGraphics.html) which has become a common interface to draw text and shapes on both Terminals and Screens (also on the upcoming GUI system).
  * I've published the javadocs online too, both for 2.1 and 3.0, please see the link section to the left of here.
  * It seems like Lanterna had a brief moment of fame over at [Hacker News](https://news.ycombinator.com/item?id=8042106)!

**2014-07-13**
  * First alpha release of Lanterna 3.0 is available for anyone curious to see what coming next
    * Google has removed the ability to upload files so you need to get this one from Maven (same artifact as above, but version is 3.0.0-alpha1)
    * For more details on what has changed, see the [wiki](https://code.google.com/p/lanterna/wiki/ChangesFrom2to3)

**2014-06-09**
  * Minor patch release 2.1.8 released!
  * The changelog is available [here](https://code.google.com/p/lanterna/wiki/ChangesFrom217to218)
  * Unfortunately, Google has taken away the ability to upload files for the Download tab, so you'll have to get this version from Maven or clone the repository and compile it locally

**2014-04-13**
  * Video tutorial posted on YouTube by Matthew Nickson, see it [here](https://www.youtube.com/watch?v=bvK7AdhqO3A)
  * Work on 3.0 is progressing, the terminal and screen APIs are starting to stabilize, please check out the code from the repository and give it a try!

**2014-02-23**
  * Source version control system changed from Mercurial to Git

**2013-12-08**
  * Lanterna 2.1.7 released!
  * The changelog is available [here](https://code.google.com/p/lanterna/wiki/ChangesFrom216to217)
  * Google Code is removing the ability to upload files on January 15 2014, so this may be the last release I upload here. Will try to figure out how to migrate the files to Drive.

**2013-08-22**
  * Lanterna 2.1.6 released!
  * The changelog is available [here](https://code.google.com/p/lanterna/wiki/ChangesFrom215to216)

**2013-04-20**
  * Lanterna 2.1.5 released!
  * The changelog is available [here](https://code.google.com/p/lanterna/wiki/ChangesFrom213to215)

**2013-02-19**
  * Lanterna 2.1.3 released!
  * The changelog is available [here](https://code.google.com/p/lanterna/wiki/ChangesFrom212to213)

**2013-01-04**
  * Lanterna 2.1.2 released!
  * The changelog is available [here](https://code.google.com/p/lanterna/wiki/ChangesFrom211to212)

**2012-10-28**
  * Lanterna 2.0.4 released!
  * Minor bugfix release, quite possibly the last 2.0.x
  * The changelog is available [here](https://code.google.com/p/lanterna/wiki/ChangesFrom203to204)

**2012-10-07**
  * Lanterna 2.1.1 released!
  * The changelog is available [here](https://code.google.com/p/lanterna/wiki/ChangesFrom210to211)

**2012-09-17**
  * Lanterna 2.1.0 released!
  * You can see a brief list of changes [here](https://code.google.com/p/lanterna/wiki/ChangesFrom20Xto210)
  * Development for 2.2.x is starting on the default branch, 2.1.x will continue on 2.1.x-branch

**2012-09-09**
  * Closing in on the 2.1.0 release, please report any issues you are having on the Issue tracker here or by email to the [lanterna-group](http://groups.google.com/group/lanterna-discuss).

**2012-08-09**
  * Bugfix release 2.0.3 deployed to Maven and uploaded to the `Downloads` page here. A simple [changelog](http://code.google.com/p/lanterna/wiki/ChangesFrom201to203) is available.

**2012-07-18**
  * Updated most of the Wiki documentation to 2.0.x (what was already there, that is... still missing large segments on the GUI system)
  * Forgot to link to [Clojure-lanterna](http://sjl.bitbucket.org/clojure-lanterna/)! It's a Lanterna wrapper to make it easier to use with Clojure. Programming terminals doesn't get more exciting than this, go check it out!

**2012-07-15**
  * Lanterna 2.0.1 has been released. Bugfixes and a few new features, please let it take a few hours to sync to Maven Central. A [list of changes](http://code.google.com/p/lanterna/wiki/ChangesFrom200to201) is available on the wiki.

**2012-07-07**
  * Lanterna 2.0.0 and 1.0.6 released! You can find the downloads here and through Maven (may take a few hours before it has reached all Maven mirrors)
  * 2.0.0 is a major revision of the Lanterna API, give it a try and please report any issues you find. It is not API compatible with the 1.0.x releases
  * 1.0.6 is a minor increment, allowing you to choose which font the Swing terminal emulator will use

**2012-06-20**
  * First release through Maven and Sonatype! You can now set a dependency on `com.googlecode.lanterna` version 1.0.5, once it has synchronized to the central repository.
  * `2.0-SNAPSHOT` is available in the snapshots repository at Sonatype as well

**2012-06-16**
  * Applied for Maven hosting at Sonatype OSS, see [OSSRH-3751](https://issues.sonatype.org/browse/OSSRH-3751)
  * Merged the 2.0 preparation branch to trunk, 2.0 release expected soon!

**2012-05-27**
  * Good progress on the upcoming 2.0 release, you can follow development work in the 2.0-prep-work branch.

**2012-05-06**
  * 1.0.3 released, including one submitted bugfix. Also deprecated a lot of old versions from the Downloads page. Next release is going to have some major API changes and for this reason it will probably be named 2.0. Created a Wiki [page](ChangesFrom1to2.md) to describe the changes.

**2011-12-11**
  * 1.0.2 released, please give it a try, report any bugs on the issue tracker or to the email group

**2011-11-30**
  * Adding a user-supplied screenshot from Mac OS X (thank you!)
  * 1.0.2 should be packaged and released soon, with some more minor bugfixes contributed from users (thank you again!)

**2011-10-31**
  * Released 1.0.1 with a few bugfixes (of which I can take no credit, all thanks to a friendly user who sent me patches; thank you very much!)
**2011-10-16**
  * Finally built the 1.0 release. With no changes for four months, I think it's time.
  * Created a 1.0.x branch for bugfixes
  * I've changed the license to LGPL, which it should have been from the beginning.
  * While the Wiki is still a bit out of date, I've added some JavaDoc to most of the class declarations (outside of the gui package)

**2011-06-14**
  * Release candidate 2 out, I'm not expecting much more to change now. Included is better screen resolution calculations and possibly some Swing GUI fixes.
  * Seems like the Wiki has been a bit distorted since Google code changed their look & feel, apologies for this, I'll try to restore it shortly.

**2011-03-02**
  * Lanterna can now figure out the screen resolution without using the terminal symbol in /dev, I felt this was important enough to include in 1.0 so the release is going to be postponed for a bit longer...

**2010-11-15**
  * Lantern 1.0 release candidate 1 released

**2010-10-13**
  * Lantern 1.0 beta 3 release
  * The panel layout system has been reworked and needs a bit of time to cool down before a release candidate can be tagged.

**2010-07-21**
  * A lot of documentation has been written, but it's still not completely finished. Not a lot of code changes since beta 2, maybe a release candidate will be tagged not before too long.

**2010-06-28**
  * Discussion group [lanterna-discuss](http://groups.google.se/group/lanterna-discuss) created
  * Lanterna 1.0 beta 2 released

**2010-06-25**
  * Lanterna 1.0 beta 1 released
  * Project uploaded to Google Code