# Changes in 2.1.6 #
Lanterna 2.1.6 is a minor bugfix release with a few enhancements
  * Proper `.equals` and `.hashCode` for `com.googlecode.lanterna.input.Key`
  * Proper `.equals` and `.hashCode` for `com.googlecode.lanterna.terminal.TerminalPosition`
  * Experimental TextArea, a user-contributed component
  * `SwingTerminal` AWT threading fixes
  * `ActionListBox` has a new parameter that closes the dialog before running the selected `Action`
  * New methods to `Screen` to better handle resizes. You can call `.updateScreenSize()` to manually check and update the internal data structures and allowing you to redraw the screen before calling `.refresh()`.
  * Fixed deadlock in `GUIScreen`

For a more complete list of changes, please click on the Source tab above and browse the changesets.