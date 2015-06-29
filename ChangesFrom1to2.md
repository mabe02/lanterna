# Changes in 2.0.0 #

Here is a brief (and probably incomplete) list of the API changes from version 1.0.x to the 2.0.0 release.

  * TermInfo classes are now gone (they didn't really work so hopefully no one was using them)
  * Mavenized the project, will try to push it to Maven central somehow
  * Renamed the project's package name from `org.lantern` to `com.googlecode.lanterna`
  * ~~Moved `com.googlecode.lanterna.TerminalFactory` to `com.googlecode.lanterna.terminal` where it belongs~~
  * Renamed `LanternException` to `LanternaException` for consistency
  * `LanternaException` is now a `RuntimeException` since `IOException`s coming from the underlying stdin and stdout are quite rare
  * `Terminal.addInputProfile(...)` moved to `InputProvider`
  * `Terminal.Style` has been moved to an outer class in `com.googlecode.lanterna.screen`
  * `SwingTerminal` has been moved to `com.googlecode.lanterna.terminal.swing`
  * Re-arranged the `Terminal` hierarchy, this is mostly internals but you might have been using `CommonUnixTerminal` before, which is now known as `com.googlecode.lanterna.terminal.text.UnixTerminal`
  * Moved `setCBreak` and `setEcho` off the `Terminal` interface to the `ANSITerminal` class. You probably don't need to call these directly anyway, since they are automatically called for the `UnixTerminal` when entering private mode.
  * Removed the `LanternTerminal` and `TerminalFactory` classes as they were quite confusing and not really necessary
  * Added a new facade class, `TerminalFacade` which provides some convenient methods for creating terminal objects
  * Experimental support for Cygwin, but not very functional at the moment
  * Renaming some enums and an internal class in `Theme`, you probably won't be affected by this unless you have defined your own theme
  * `Interactable.Result` and `Interactable.FocusChangeDirection` has been expanded to allow focus switching in four directions instead of two
  * Introduced `AbstractListBox` which has standardized the format and the methods of the list-based GUI elements
  * `ListBox` class removed, there's not much purpose for it in this environment
  * `RadioCheckBox` and `RadioCheckBoxGroup` has been removed and replaced by `RadioCheckBoxList`

## Maven ##
Starting with the 2.0.0 release, Lanterna has been using Maven and the Sonatype OSS repository, which is synchronized with Maven central. Please see the [maven](Maven.md) information page for more details.