# Changes in 2.0.1 #

Here is a brief list of the changes from version 2.0.0 to 2.0.1. Please note that the versions are API compatible, i.e. if your code works with 2.0.0 it will work with 2.0.1 without changing the code.

  * `Screen` now has a `clear()` method that will reset the content of the screen
  * Added new overloads so that you can specify a separate font to use for bold text in `SwingTerminal`
  * `SwingTerminal` will render underlined text now
  * `Terminal` interface has a new method `getTerminalSize()` that will synchronously retrieve the current size of the terminal
  * `queryTerminalSize()` has been marked as deprecated but will still work as before
  * `SwingTerminal` will expose its internal `JFrame` through a new method `getJFrame()` so that you can set a custom title, icon, image list and so on
  * `TextBox` and `PasswordBox` constructors that didn't take a width parameter were broken, fixed and changed so that an initial size (unless forced) will be at least 10 columns wide

For a more complete list of changes, please click on the **Source** tab above and browse the changesets.