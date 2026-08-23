package com.googlecode.lanterna;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class TextColorTest {
    @Test
    public void testFromAWTColor() {
        TextColor.RGB rgb = TextColor.RGB.fromAWTColor(Color.BLUE);
        assertEquals(0, rgb.getRed());
        assertEquals(0, rgb.getGreen());
        assertEquals(255, rgb.getBlue());
        rgb = TextColor.RGB.fromAWTColor(Color.RED);
        assertEquals(255, rgb.getRed());
        assertEquals(0, rgb.getGreen());
        assertEquals(0, rgb.getBlue());
        rgb = TextColor.RGB.fromAWTColor(Color.GREEN);
        assertEquals(0, rgb.getRed());
        assertEquals(255, rgb.getGreen());
        assertEquals(0, rgb.getBlue());
    }
}