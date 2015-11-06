Terminal Overview
---
The Terminal layer is the lowest level available in Lanterna, giving you very precise control of what data is sent to the client. You will find these classes in the `com.googlecode.lanterna.terminal` package.

### Getting the `Terminal` object

The core of the Terminal layer is the Terminal interface which you must retrieve. This can be done either by directly instantiating one of the implementing concrete classes (UnixTerminal, CygwinTerminal or SwingTerminal) or by using the `DefaultTerminalFactory` to create a terminal:

```
	Terminal terminal = new DefaultTerminalFactory().createTerminal();
```

### What's the big deal anyway?

So, what's the difference between these implementations of `Terminal`? If, by terminal, you think of applications like xterm, konsole, putty or Terminal.app, then using Lanterna with a terminal would involve writing control sequences to standard output (stdout) and reading from standard input (stdin). You would run your Java application in this terminal and calling the various methods on the `Terminal` interface will be translated by the implementation to the equivalent control sequences. But a terminal could be anything and we don't want to lock ourselves to a particular idea of what a terminal is (something using stdout/stdin for example). Instead, in Lanterna, a terminal is something that implements `Terminal` and with a correct implementation of the interface, the entire stack (Screen and GUIScreen) will work on top of it, without any modifications.

### The `SwingTerminal`

One alternative implementation of `Terminal` is the `SwingTerminal` class. This is a basic terminal emulator implemented in Swing. It will create a JFrame that represents your terminal. Calling the Terminal methods on a SwingTerminal won't do anything with stdout or stdin, instead it will translate it to paint operations on the JFrame. This is useful, for example, when you are developing your application that uses Lanterna. You are probably using an IDE like Eclipse, NetBeans or IntelliJ. The console windows in these IDE's don't support any of the control sequences the UnixTerminal will, such as writing to stdout when you try to move around the cursor or change the color of the text. By using `SwingTerminal`, running the application will instead create a new window that emulates a terminal so that you can develop and debug like you would do when developing generic Java Swing applications.