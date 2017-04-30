# Start the GUI
## Getting the `WindowBasedTextGUI` object ###
Lanterna has a basic `TextGUI` interface and a slightly more extended `WindowBasedTextGUI` interface you will be using
when working with the GUI system built-in. In reality though, there is only one concrete implementation at this point,
`MultiWindowTextGUI` so there isn't really case where you would use `TextGUI` over `WindowBasedTextGUI`.

To instantiate, you need a `Screen` object that the GUI will render too:

    WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);

## Threading concerns
Usually GUI APIs have threading issues of some sort, many simply give up and declare that all modifications had to be
done on a designated "GUI thread". This generally a reasonably approach and while Lanterna doesn't enforce it 
(internal API is synchronized on a best-effort basis), it's recommended. When you create the text GUI on your code,
you can make a decision about if you want Lanterna to create its own GUI thread will be responsible for all the drawing
operations or if you want your own thread to do this. The managed GUI thread is slightly more complicated so in this
guide we are going to use the latter. While most of the API remains the same (the separate GUI thread approach will
require manually starting and stopping), whenever there are differences these will be pointed out.
 
By default, when you create a `MultiWindowTextGUI` like in the previous section, Lanterna will use the same-thread 
strategy.

## Starting the GUI
Unlike `Screen`, the `TextGUI` interface doesn't have start or stop methods. It will simply use the `Screen` object you
pass in as-is. Because of this, you'll want to start the screen before attempting to draw anything with the GUI.

    Terminal term = new DefaultTerminalFactory().createTerminal();
    Screen screen = new TerminalScreen(term);
    WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);
    screen.startScreen();
    
    // use GUI here until the GUI wants to exit
    
    screen.stopScreen();
    
## Next
Continue to [Basic windows](GUIGuideWindows.md)