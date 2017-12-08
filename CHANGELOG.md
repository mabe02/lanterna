# Changelog

## Table of contents
* [**3.0.0**](#3.0.0)
* [2.1.9](#2.1.9)
* [2.1.8](#2.1.8)
* [2.1.7](#2.1.7)
* [2.1.6](#2.1.6)
* [2.1.5](#2.1.5)
* [2.1.3](#2.1.3)
* [2.1.2](#2.1.2)
* [2.1.1](#2.1.1)
* [**2.1.0**](#2.1.0)
* [2.0.4](#2.0.4)
* [2.0.3](#2.0.3)
* [2.0.2](#2.0.3)
* [2.0.1](#2.0.1)
* [**2.0.0**](#2.0.0)

## 3.0.0
Lanterna 3 is a large, and probably final, update to the Lanterna library. Many parts have been completely rewritten and the parts not rewritten have been touched in at least some way. The reason for this major overhaul is to finally get it 'right' and fix all those API mistakes that have been highlighted over the years since Lanterna was first published.

This section can in no way summarize all the changes but will try to highlight some of the new features and redesigns.
Please note that Lanterna 3 is **not** API compatible with Lanterna 2.X and earlier.

**NOTE**: Lanterna 3 is still under development. The majority of the features below are implemented but not all.

## Added
* Proper support for CJK characters and handling of them
* New GUI system: The old GUI system has been deprecated and a new one is replacing it, giving you much more control over how you want your GUI to look. You can do any kind of old-school interface, not just dialog-based ones and even things like multi-tasking windows if you like. Please note that this is currently under development.
* New `SwingTerminal`: `SwingTerminal` in Lanterna 2.X was limited in many ways. For Lanterna 3.0 some of those limitations have been addressed. The actual class is no longer a `JFrame` but a `JComponent`, meaning you can easily embed it into any Swing application. Furthermore, it does not require to be run in private mode anymore. You can switch between normal and private mode as you like and it will keep track of the content. Additionally, it finally supports a backlog history and scrolling. A helper class, `ScrollingSwingTerminal`, can easily get you started with this. If you want the classic behaviour there is `SwingTerminalFrame` which behaves much like `SwingTerminal` used to.
* Telnet server: In addition to the terminal implementations that have been around since the earlier builds of Lanterna, version 3 introduces a Telnet server class that allows you to program multiple terminals against clients connecting in through standard Telnet. A small subset of the Telnet protocol is implemented so far, however, it supports features such as window resizing, line mode setting and echo control.
* `ScreenWriter` now supports not just text and filled rectangles but also lines and both filled and unfilled triangles.

## Changed
* Made `Screen` an interface and cleaned up its API. The default implementation behaves like `Screen` used to with improvements such as full color support
* The code and API more closely follows Java conventions on naming and style

## 2.1.9
### Added
* Better ESC key detection
* Enable EOF 'key' when the input stream is closed (requires setting system property 'com.googlecode.lanterna.enable-eof' to 'true')
* `TextBox` now accepts input of non-Latin characters

### Changed
* Better ESC key detection
* Regression fixed with high CPU load when opening a window with no interactable components
* `KeyMappingProfile` patterns now public

## 2.1.8
### Added
* Ability to set the fill character of `TextBox` components (other than space)
* Ability to disable shadows for windows
* Added a file dialog component
* Added a method to make it easier to wrap components in a border
* Added `SwingTerminal` function key support
* Window-deriving classes can inspect which component has input focus

### Changed
* Input focus bug fixes
* `InputDecoder` fixes backported from master branch

## 2.1.7
### Added
* Added support for the PageUp, PageDown, Home and End keys inside `AbstractListBox` and its subclasses

### Changed
* Change visibility of `LayoutParameter` constructor to public, making it easier to create custom layout managers
* Fixed `TextArea` crash on pressing End when horizontal size is too big
* Miscellaneous bug fixes
* Terminals will remember if they are in private mode and will not attempt to enter twice
* `Screen` will drain the input queue upon exiting

## 2.1.6
### Added
* Added an experimental `TextArea`, a user-contributed component
* Added `Screen.updateScreenSize()` to manually check and update internal data structures, allowing you to redraw the screen before calling `Screen.refresh()`
* Proper `Key.equals(...)` and `Key.hashCode()` methods
* Proper `TerminalPosition.equals(...)` and `TerminalPosition.hashCode()` methods

### Changed
* Fixed a deadlock in `GUIScreen`
* `ActionListBox` has a new parameter that closes the dialog before running the selected `Action`
* `SwingTerminal` AWT threading fixes

## 2.1.5
### Added
* Added a new method to invalidate the `Screen` buffer and force a complete redraw

### Changed
* Visibility changed on `GUIScreen` to make it easier to extend

## 2.1.3
### Added
* Customization of screen padding character
* More input key combinations detecting ALT down

### Changed
* Background color fix with `Screen`
* Expanded `Table` API
* Improved (but still incomplete) CJK character handling
* OS X input compatibility fix

## 2.1.2
### Added
* `RadioCheckBoxList.getCheckedItem()`

### Changed
* Enhanced restoration of the terminal control codes (especially on Solaris)
* Fixed a bug that occurred when `SwingTerminal` is reduced to 0 rows
* Fixed a bug that prevented the cursor from becoming visible again after leaving private mode
* `ActionListDialog` now increases in size as you add items
* `TextBox` can now tell you the current edit cursor position

## 2.1.1
### Added
* Re-added `GUIScreen.closeWindow()` (as deprecated)
* Re-added `Panel.setBetweenComponentsPadding(...)` (as deprecated)

### Changed
* Owner window can now be correctly derived from a component
* Classes extending `AbstractListBox` now follow the preferred size override correctly

### Added
* Added a new component, `ActivityIndicator`
* Added support for showing and hiding the text cursor
* Included ANSI colour palettes for the `SwingTerminal` to mimic the appearance of several popular terminal emulators
* Introduced the `BorderLayout` layout managed
* Support 8-bit and 24-bit colours (not supported by all terminal emulators)
* Support detection of CTRL and ALT key status
* `GUIScreen` backgrounds can now be customized

### Changed
* Close windows using `Window.close()` instead of `GUIScreen.closeWindow(...)`
* Generalized component alignment
* GUI windows can now be display in full-screen mode, taking up the entire terminal
* Lots of bug fixes
* Reworked GUI layout system
* Reworked the theme system
* Window size is overridable
* `SwingTerminal` now uses a new class, `TerminalAppearance`, to retrieve the visual settings, such as fonts and colours

### Removed
* Removed dependencies on proprietary Sun API

## 2.1.0
2.1.X is **not** strictly API compatible with 2.0.X but compared to going from 1.0.X to 2.0.X there will be fewer API breaking changes

## 2.0.4
### Added
* The PageUp, PageDown, Home, and End keys now work in the `TextArea` component

### Changed
* Adding rows to a `Table` will trigger the screen to redraw
* Improved API for `RadioCheckBoxList`

## 2.0.3
### Added
* Added experimental support for F1-F12 keys
* `TextArea` can now be modified (experimental feature)

### Changed
* Font fixes. Hopefully it will look better on Linux now
* Invisible components no longer receive focus
* The size policies are working better now but they are still somewhat mysterious. I will try to come up with something better for the 2.1.0 release

### What about 2.0.2?
There is no 2.0.2. I did a mistake staging the new release and had to start over again but 2.0.2 had already been tagged in Mercurial so I could not re-release it. Instead we skipped a number and landed on 2.0.3

## 2.0.1
### Added
* Added `Screen.clear()` that allows resetting the content of the screen
* Added `Terminal.getTerminalSize()` to synchronously retrieve the current size of the terminal
* Added new overloads so that you can specify a separate font to use for bold text in `SwingTerminal`
* `SwingTerminal` will now render underlined text
* `SwingTerminal` will expose its internal `JFrame` through a new method `getJFrame()`, allowing you to set a custom title, icon, image list etc.

### Changed
* `queryTerminalSize()` has been marked as deprecated but will still work as before
* `TextBox` and `PasswordBox` constructors that did not take a width parameter were broken, fixed and changed so that the initial size (unless forced) will be at least 10 columns wide

## 2.0.0
### Added
* Added a new facade class, `TerminalFacade`, which provides some convenience methods for creating terminal objects
* Added experimental, but not very functional, support for Cygwin
* Expanded `Interactable.Result` and `Interactable.FocusChangeDirection` to allow focus switching in four directions instead of only two
* Introduced `AbstractListBox` which has standardized the format and the methods of the list-based GUI elements
* Mavenized the project, will try to push it to Maven Central somehow

### Changed
* ~~Moved `com.googlecode.lanterna.TerminalFactory` to `com.googlecode.lanterna.terminal` where it belongs~~
* Moved `Terminal.addInputProfile(...)` to `InputProvider`
* Moved `Terminal.Style` to an outer class in `com.googlecode.lanterna.screen`
* Moved `SwingTerminal` to `com.googlecode.lanterna.terminal.swing`
* Moved `Terminal.setCBreak(...)` and `Terminal.setEcho(...)` into `ANSITerminal`. You probably don't need to call these directly anyway, since they are automatically called for the `UnixTerminal` when entering private mode
* Rearranged the `Terminal` hierarchy. This is mostly internal but you might have been using `CommonUnixTerminal` before which is now known as `com.googlecode.lanterna.terminal.text.UnixTerminal`
* Renamed the project's package name from `org.lantern` to `com.googlecode.lanterna`
* Renamed `LanternException` to `LanternaException` for consistency
* `LanternaException` is now a `RuntimeException` since `IOException`s coming from stdin and stdout are quite rare
* Renamed some enums and an internal class in `Theme`. You probably will not be affected by this unless you have defined your own theme

### Removed
* Removed `LanternTerminal` and `TerminalFactory` as they were quite confusing and not really necessary
* Removed `ListBox` as there is not much purpose for it in this environment
* Removed `RadioCheckBox` and `RadioCheckBoxGroup`. `RadioCheckBoxList` acts as a replacement
* Removed `TermInfo` classes (they did not really work so hopefully no one was using them)

### Maven
Starting with the 2.0.0 release, Lanterna has been using Maven and the Sonatype OSS repository which is synchronized with Maven Central. Please see the [Maven information page](Maven.md) for more details
