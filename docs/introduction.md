## Introduction
This document will give you a general introduction to how text terminals work and how Lanterna interacts with them. 
In the sub-documents, you can find specific guides on how to program each one of the layers. 
You are encouraged to read them all and in order to get the full picture.

## About terminals
### TERM and terminfo 
The way terminals work make things difficult when you want to create a portable text GUI. Initially, way back, each 
computer system might have its own set of control characters, a kind of escape sequence, that signalled to the screen 
that a special action (such as setting the text color to red) was about to happen. With different systems and different 
environments, it was tough to write a program that would work everywhere and have the same appearance (well, if you stick 
to just printing text, that would probably be fine, but if you wanted to make use of more advanced commands such as moving the 
cursor, setting the color of the text, making the text blink or change to bold font, etc, then you had a bit of a 
challenge.

There was then an idea that when a terminal logs on to a system, it will set the TERM environmental variable to a 
particular value that represented which standard it supported. On the system is a database with different standards 
and so any program using text magic can look up the TERM value in this database and get a list of commands supported and 
how to execute them. There is however no guarantee that the terminal will set this variable and there is no guarantee 
that the system will have the value in its database. Also, there may be discrepancies when, for example, several 
implementations of `xterm` exists, which one do you store in your database (the implementations mostly agree on the 
control characters, but not completely)?

Today, most terminals will identify themselves as `xterm` through the TERM environmental variable and for the most part 
support this. There is also an [ANSI standard](http://en.wikipedia.org/wiki/ANSI_escape_code) for escape codes and this 
is very much in line with what most terminal emulators support. Incompatibilities arise mostly when it comes to 
special keys on the keyboard, such as insert, home, end, page up, and so on (what the terminal emulator will send to 
standard input when those keys are pressed).

### Lanterna
Lanterna will use the `xterm` standard as most terminals understand it, which is basically the standard 
[ANSI escape codes](http://en.wikipedia.org/wiki/ANSI_escape_code). As notes in the previous passage explained, where terminals 
diverge most from the `xterm` specification is in the escape codes for all the special keyboard keys. At the moment, Lantern 
tries to accommodate for all of them by adding 'input profiles' for each known type. As new terminals (emulators) are 
added, the profiles might need to be updated. So far, as a developer you shouldn't need to mess around with these 
profiles as there are no key collisions and we'll try to add any popular terminal emulator out there. The idea is that 
Lanterna should work right out of the box, without any tweaking to make it work with your favourite terminal emulator.

Some more exotic extensions are also supported, such as mouse support and colors above the standard ANSI 8+8. When using
this functionality though, it's likely that your application will have compatibility issues with some common terminal
emulators used in the wild. A good rule of thumb is that if it works with vanilla `xterm`, it will probably be 
relatively well-supported.

### Encoding
Another problem is the encoding, where terminals may or may not support UTF-8 no matter how you tweak it. By default, 
Lanterna will use the system property `file.encoding`, which is setup automatically by the JVM. This seems to be 
sufficient with most terminals, but you may want to present the user with an option to force either UTF-8 or iso-8859-1
(or whatever you see suitable), maybe through a command-line argument.

## How big is the terminal?
Yet another problem is how to know the size of the terminal. There are ways of figuring this out through C APIs for most 
platforms but since Lanterna is intended to be 100% Java code, we can't do that. Instead, Lanterna is doing a bit of a 
hack by memorizing the cursor location, then moving it to 5000x5000 and asking the terminal to print the position. Since 
most terminal won't be that big, the cursor will end up in the bottom right corner and report this position when we ask 
to print it. This will be the size of the terminal.

### What if it doesn't work?
Let us know!

### What happens if the user resizes the window?
There is a special Unix signal to notify an application that the terminal has been resized and that one is WINCH. This 
is currently implemented using `sun.misc.SignalHandler` which is SUN JRE specific code and probably not portable. Still, 
catching signals without invoking native code seems difficult and relying on JNI calls is something we don't want to do. 
This will probably be changed to be done through Java reflections through auto-detection by the JVM.

## Learn more
To learn more about how to code against the Lanterna API, continue reading at [Direct terminal access](using-terminal.md)
