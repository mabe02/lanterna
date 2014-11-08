package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;

/**
 * Created by martin on 19/10/14.
 */
public class MiscComponentTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new MiscComponentTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("Grid layout test");

        Panel leftPanel = new Panel();
        Panel checkBoxPanel = new Panel();
        for(int i = 0; i < 4; i++) {
            CheckBox checkBox = new CheckBox("Checkbox #" + (i+1));
            checkBoxPanel.addComponent(checkBox);
        }

        Panel textBoxPanel = new Panel();
        textBoxPanel.addComponent(Panels.horizontal(new Label("Normal:   "), new TextBox(new TerminalSize(12, 1), "Text")));
        textBoxPanel.addComponent(Panels.horizontal(new Label("Password: "), new TextBox(new TerminalSize(12, 1), "Text").setMask('*')));

        leftPanel.addComponent(checkBoxPanel.withBorder(Borders.singleLine("CheckBoxes")));
        leftPanel.addComponent(textBoxPanel.withBorder(Borders.singleLine("TextBoxes")));

        Panel rightPanel = new Panel();
        textBoxPanel = new Panel();
        TextBox readOnlyTextArea = new TextBox(new TerminalSize(16, 8));
        readOnlyTextArea.setReadOnly(true);
        readOnlyTextArea.setText(downloadGPL());
        textBoxPanel.addComponent(readOnlyTextArea);
        rightPanel.addComponent(textBoxPanel.withBorder(Borders.singleLine("Read-only")));

        Panel contentArea = new Panel();
        contentArea.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        contentArea.addComponent(Panels.horizontal(leftPanel, rightPanel));
        contentArea.addComponent(new Separator(Direction.HORIZONTAL).setPreferredSize(new TerminalSize(16, 1)));
        contentArea.addComponent(new Button("OK", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }));
        window.setComponent(contentArea);
        textGUI.getWindowManager().addWindow(window);
    }

    private String downloadGPL() {
        try {
            URL url = new URL("http://www.gnu.org/licenses/gpl.txt");
            InputStream inputStream = url.openStream();
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[32 * 1024];
                int readBytes = 0;
                while(readBytes != -1) {
                    readBytes = inputStream.read(buffer);
                    if(readBytes > 0) {
                        byteArrayOutputStream.write(buffer, 0, readBytes);
                    }
                }
                return new String(byteArrayOutputStream.toByteArray());
            }
            finally {
                inputStream.close();
            }
        }
        catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            return stringWriter.toString();
        }
    }
}
