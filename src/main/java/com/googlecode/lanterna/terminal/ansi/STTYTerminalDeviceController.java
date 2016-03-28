package com.googlecode.lanterna.terminal.ansi;

import com.googlecode.lanterna.TerminalSize;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by martin on 27/03/16.
 */
public class STTYTerminalDeviceController implements TerminalDeviceControlStrategy {
    private final File ttyDev;
    private String sttyStatusToRestore;

    public STTYTerminalDeviceController(File ttyDev) {
        this.ttyDev = ttyDev;
        this.sttyStatusToRestore = null;
    }

    @Override
    public void saveTerminalSettings() throws IOException {
        sttyStatusToRestore = exec(getSTTYCommand(), "-g").trim();
    }

    @Override
    public void restoreTerminalSettings() throws IOException {
        if(sttyStatusToRestore != null) {
            exec(getSTTYCommand(), sttyStatusToRestore);
        }
    }

    @Override
    public void keyEchoEnabled(boolean enabled) throws IOException {
        exec(getSTTYCommand(), enabled ? "echo" : "-echo");
    }

    @Override
    public void canonicalMode(boolean enabled) throws IOException {
        exec(getSTTYCommand(), enabled ? "icanon" : "-icanon");
        if(!enabled) {
            exec(getSTTYCommand(), "min", "1");
        }
    }

    @Override
    public void keyStrokeSignalsEnabled(boolean enabled) throws IOException {
        if(enabled) {
            exec(getSTTYCommand(), "intr", "^C");
        }
        else {
            exec(getSTTYCommand(), "intr", "undef");
        }
    }

    @Override
    public TerminalSize getTerminalSize() throws IOException {
        // We could read the terminal dimensions through stty, but let's not, as it would involve a lot of spawning new
        // processes. Better to use the ANSI escape code hack
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

    protected String getSTTYCommand() {
        return "/bin/stty";
    }

    protected String exec(String... cmd) throws IOException {
        if (ttyDev != null) {
            //Here's what we try to do, but that is Java 7+ only:
            // processBuilder.redirectInput(ProcessBuilder.Redirect.from(ttyDev));
            //instead, for Java 6, we join the cmd into a scriptlet with redirection
            //and replace cmd by a call to sh with the scriptlet:
            StringBuilder sb = new StringBuilder();
            for (String arg : cmd) { sb.append(arg).append(' '); }
            sb.append("< ").append(ttyDev);
            cmd = new String[] { "sh", "-c", sb.toString() };
        }
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
}
