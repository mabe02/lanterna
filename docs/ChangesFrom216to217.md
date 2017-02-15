# Changes in 2.1.7 #
Lanterna 2.1.7 is a minor bugfix release with a few enhancements

  * Fixed `TextArea` crash on pressing End when horizontal size is too big
  * `Screen` will drain the input queue upon exiting
  * Terminals will remember if they are in private mode and won't attempt to enter twice
  * `LayoutParameter` constructor is public, to make it easier to create custom layout managers
  * `AbstractListBox` (and all subclasses) now supports PageUp, PageDown, Home and End key input
  * Misc bugfixes

For a more complete list of changes, please click on the Source tab above and browse the changesets.