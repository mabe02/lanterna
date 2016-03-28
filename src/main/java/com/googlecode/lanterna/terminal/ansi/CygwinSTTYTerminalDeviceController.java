package com.googlecode.lanterna.terminal.ansi;

import com.googlecode.lanterna.TerminalSize;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by martin on 27/03/16.
 */
public class CygwinSTTYTerminalDeviceController implements TerminalDeviceControlStrategy {

    private static final String STTY_LOCATION = findProgram("stty.exe");
    private String sttyStatusToRestore;

    public CygwinSTTYTerminalDeviceController() {
        sttyStatusToRestore = null;
    }

    /*
    private static final Pattern STTY_SIZE_PATTERN = Pattern.compile(".*rows ([0-9]+);.*columns ([0-9]+);.*");
    @Override
    public TerminalSize getTerminalSize() {
        try {
            String stty = exec(findSTTY(), "-F", getPseudoTerminalDevice(), "-a");
            Matcher matcher = STTY_SIZE_PATTERN.matcher(stty);
            if(matcher.matches()) {
                return new TerminalSize(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(1)));
            }
            else {
                return new TerminalSize(80, 24);
            }
        }
        catch(Throwable e) {
            return new TerminalSize(80, 24);
        }
    }
     */

    @Override
    public void saveTerminalSettings() throws IOException {
        sttyStatusToRestore = runSTTYCommand("-g").trim();
    }

    @Override
    public void restoreTerminalSettings() throws IOException {
        if(sttyStatusToRestore != null) {
            runSTTYCommand(sttyStatusToRestore);
        }
    }

    @Override
    public void keyEchoEnabled(boolean enabled) throws IOException {
        runSTTYCommand(enabled ? "echo" : "-echo");
    }

    @Override
    public void canonicalMode(boolean enabled) throws IOException {
        runSTTYCommand(enabled ? "icanon" : "cbreak");
        if(!enabled) {
            runSTTYCommand("min", "1");
        }
    }

    @Override
    public void keyStrokeSignalsEnabled(boolean enabled) throws IOException {
        // TBD
    }

    @Override
    public TerminalSize getTerminalSize() throws IOException {
        return null;
    }

    @Override
    public void registerTerminalResizeListener(final Runnable onResize) throws IOException {
        try {
            Class<?> signalClass = Class.forName("sun.misc.Signal");
            for(Method m : signalClass.getDeclaredMethods()) {
                if("handle".equals(m.getName())) {
                    Object windowResizeHandler = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Class.forName("sun.misc.SignalHandler")}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if("handle".equals(method.getName())) {
                                onResize.run();
                            }
                            return null;
                        }
                    });
                    m.invoke(null, signalClass.getConstructor(String.class).newInstance("WINCH"), windowResizeHandler);
                }
            }
        }
        catch(Throwable ignore) {
            // We're probably running on a non-Sun JVM and there's no way to catch signals without resorting to native
            // code integration
        }
    }

    private String runSTTYCommand(String... parameters) throws IOException {
        List<String> commandLine = new ArrayList<String>(Arrays.asList(
                findSTTY(),
                "-F",
                getPseudoTerminalDevice()));
        commandLine.addAll(Arrays.asList(parameters));
        return exec(commandLine.toArray(new String[commandLine.size()]));
    }

    protected String exec(String... cmd) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();
        ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
        InputStream stdout = process.getInputStream();
        int readByte = stdout.read();
        while(readByte >= 0) {
            stdoutBuffer.write(readByte);
            readByte = stdout.read();
        }
        ByteArrayInputStream stdoutBufferInputStream = new ByteArrayInputStream(stdoutBuffer.toByteArray());
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdoutBufferInputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }

    private String findSTTY() {
        return STTY_LOCATION;
    }

    private String getPseudoTerminalDevice() {
        //This will only work if you only have one terminal window open, otherwise we'll need to figure out somehow
        //which pty to use, which could be very tricky...
        return "/dev/pty0";
    }

    private static String findProgram(String programName) {
        String[] paths = System.getProperty("java.library.path").split(";");
        for(String path : paths) {
            File shBin = new File(path, programName);
            if(shBin.exists()) {
                return shBin.getAbsolutePath();
            }
        }
        return programName;
    }
}
