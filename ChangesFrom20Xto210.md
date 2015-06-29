# Changes in 2.1.0 #
Here is a brief list of the changes from the 2.0.x branch to 2.1.0. 2.1.x is **not** strictly API compatible with 2.0.x, but compared with going from 1.0.x to 2.0.x, there are fewer API breaking changes

  * GUI Windows can be displayed in full-screen mode, taking up the entire terminal
  * `SwingTerminal` now uses a new class, `TerminalAppearance` to get the visual settings, such as fonts and colours
  * Included ANSI colour palettes for the `SwingTerminal` to mimic the appearance of several popular terminal emulators
  * Reworked GUI layout system
  * Introduced the `BorderLayout` layout manager
  * Reworked the theme system
  * Removed dependencies on proprietary SUN API
  * Customizable `GUIScreen` backgrounds
  * Close windows using `Window.close()` instead of `GUIScreen.closeWindow(...)`
  * Generalized component alignment
  * Support detection of CTRL and ALT key status
  * Supports 8-bit and 24-bit colours (not supported by all terminal emulators)
  * Window size is overridable
  * New component `ActivityIndicator`
  * Support for showing/hiding the text cursor
  * Lots of bugfixes!

For a more complete list of changes, please click on the Source tab above and browse the changesets.