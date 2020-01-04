package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by martin on 2017-04-15.
 */
public class Issue305 {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        System.out.println("Class: " + terminal.getClass());

        terminal.close();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(System.in));

            while (true) {

                System.out.print("Enter something: ");
                String input = br.readLine();

                if ("q".equals(input)) {
                    System.out.println("Exit!");
                    System.exit(0);
                }

                System.out.println("input : " + input);
                System.out.println("-----------\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
