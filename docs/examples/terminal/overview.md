Terminal Overview
---
The Terminal layer is the lowest level available in Lanterna, giving you very precise control of what data is sent to the client. You will find these classes in the `com.googlecode.lanterna.terminal` package.

### Getting the `Terminal` object

The core of the Terminal layer is the Terminal interface which you must retrieve. This can be done either by directly instantiating one of the implementing concrete classes (UnixTerminal, CygwinTerminal or SwingTerminal) or by using the `DefaultTerminalFactory` to create a terminal:

```
	Terminal terminal = new DefaultTerminalFactory().createTerminal();
```

