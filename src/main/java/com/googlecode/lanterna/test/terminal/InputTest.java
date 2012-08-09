/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.test.terminal;

import java.io.IOException;

/**
 *
 * @author Martin
 */
public class InputTest {
    public static void main(String[] args) throws IOException {
        while(true) {
            int inByte = System.in.read();
            System.out.println(inByte);
        }
    }
}
