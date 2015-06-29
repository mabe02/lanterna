# What's new in Lanterna 3 #
Lanterna 3 is a large, and probably final, update to the Lanterna library. Many parts have been completely rewritten and the parts not rewritten has been touched in at least some way. The reason for this major overhaul is to finally get it 'right' and fix up all those API mistakes that have been highlighted over the years since lanterna was first published.

This document can in no way summarize all the changes, but I will try to highlight some of the new features and redesigns. Please note that Lanterna 3 is **not** API compatible with Lanterna 2.x and earlier.

**NOTE:** Lanterna 3 is still under development, the majority of the features below are available not but all!

## New SwingTerminal ##
SwingTerminal in 2.0 was very limited in many ways, for 3.0 some of those limitations have finally been adressed. The actual class is no longer a JFrame but a JComponent, which means you can easily embedd it into any Swing application. Furthermore, it doesn't require to be running in private mode only anymore, you can switch between normal mode and private mode as you like and it will remember the content. Furthermore, it finally supports a backlog history and scrolling, a helper class ScrollingSwingTerminal can easily get you started with this. If you want the classic behaviour, there's SwingTerminalFrame which behaves much like SwingTerminal used to.

## New Screen ##
The Screen class has become an interface and it's API is cleared up. There is a default implementation available that behaves like Screen used to, although with a few improvements such as full color support.

## Better text graphics support ##
In order to help out to make text graphic shapes, ScreenWriter supports not just text and filled rectangles, but also lines and triangles (filled and unfilled).

## New GUI system ##
The old GUI system has been deprecated and a new one is replacing it, giving you much more control over how you want your GUI to look. You can do any kind of old-school interface, not just dialogs-based, and even things like multi-tasking windows if you like.
_Currently under development_

## Telnet server ##
In addition to the terminal implementations that has been around since the earlier builds of lanterna, version 3 introduces a telnet server class that lets you program multiple Terminals agains clients connecting in through standard telnet. A small subset of Telnet protocol is implemented so far, but it supports features such as windows resizing, line mode setting and echo control.

## CJK and wide-character support ##
The whole lanterna stack now has proper support for CJK characters and handling of them.

## Java conventions ##
The code and API more closely follows Java conventions on naming and style.