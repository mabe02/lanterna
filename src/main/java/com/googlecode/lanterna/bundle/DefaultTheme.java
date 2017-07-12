package com.googlecode.lanterna.bundle;

import com.googlecode.lanterna.graphics.PropertyTheme;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

class DefaultTheme extends PropertyTheme {
    DefaultTheme() {
        super(definitionAsProperty(), false);
    }

    private static Properties definitionAsProperty() {
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(definition));
            return properties;
        }
        catch(IOException e) {
            // We should never get here!
            throw new RuntimeException("Unexpected I/O error", e);
        }
    }

    private static final String definition = "# This is the default properties\n" +
            "\n" +
            "foreground = black\n" +
            "background = white\n" +
            "sgr        =\n" +
            "foreground[SELECTED] = white\n" +
            "background[SELECTED] = blue\n" +
            "sgr[SELECTED]        = bold\n" +
            "foreground[PRELIGHT] = white\n" +
            "background[PRELIGHT] = blue\n" +
            "sgr[PRELIGHT]        = bold\n" +
            "foreground[ACTIVE]   = white\n" +
            "background[ACTIVE]   = blue\n" +
            "sgr[ACTIVE]          = bold\n" +
            "foreground[INSENSITIVE] = white\n" +
            "background[INSENSITIVE] = blue\n" +
            "sgr[INSENSITIVE]     =\n" +
            "\n" +
            "# By default use the shadow post-renderer\n" +
            "postrenderer = com.googlecode.lanterna.gui2.WindowShadowRenderer\n" +
            "\n" +
            "#Borders\n" +
            "com.googlecode.lanterna.gui2.AbstractBorder.background[PRELIGHT] = white\n" +
            "com.googlecode.lanterna.gui2.AbstractBorder.foreground[ACTIVE] = black\n" +
            "com.googlecode.lanterna.gui2.AbstractBorder.background[ACTIVE] = white\n" +
            "com.googlecode.lanterna.gui2.AbstractBorder.sgr[ACTIVE] =\n" +
            "com.googlecode.lanterna.gui2.AbstractBorder.foreground[INSENSITIVE] = black\n" +
            "com.googlecode.lanterna.gui2.AbstractBorder.background[INSENSITIVE] = white\n" +
            "com.googlecode.lanterna.gui2.Borders$SingleLine.char[HORIZONTAL_LINE] = \\u2500\n" +
            "com.googlecode.lanterna.gui2.Borders$SingleLine.char[VERTICAL_LINE] = \\u2502\n" +
            "com.googlecode.lanterna.gui2.Borders$SingleLine.char[BOTTOM_LEFT_CORNER] = \\u2514\n" +
            "com.googlecode.lanterna.gui2.Borders$SingleLine.char[TOP_LEFT_CORNER] = \\u250c\n" +
            "com.googlecode.lanterna.gui2.Borders$SingleLine.char[BOTTOM_RIGHT_CORNER] = \\u2518\n" +
            "com.googlecode.lanterna.gui2.Borders$SingleLine.char[TOP_RIGHT_CORNER] = \\u2510\n" +
            "com.googlecode.lanterna.gui2.Borders$SingleLine.char[TITLE_LEFT] = \\u2500\n" +
            "com.googlecode.lanterna.gui2.Borders$SingleLine.char[TITLE_RIGHT] = \\u2500\n" +
            "com.googlecode.lanterna.gui2.Borders$DoubleLine.char[HORIZONTAL_LINE] = \\u2550\n" +
            "com.googlecode.lanterna.gui2.Borders$DoubleLine.char[VERTICAL_LINE] = \\u2551\n" +
            "com.googlecode.lanterna.gui2.Borders$DoubleLine.char[BOTTOM_LEFT_CORNER] = \\u255a\n" +
            "com.googlecode.lanterna.gui2.Borders$DoubleLine.char[TOP_LEFT_CORNER] = \\u2554\n" +
            "com.googlecode.lanterna.gui2.Borders$DoubleLine.char[BOTTOM_RIGHT_CORNER] = \\u255d\n" +
            "com.googlecode.lanterna.gui2.Borders$DoubleLine.char[TOP_RIGHT_CORNER] = \\u2557\n" +
            "com.googlecode.lanterna.gui2.Borders$DoubleLine.char[TITLE_LEFT] = \\u2550\n" +
            "com.googlecode.lanterna.gui2.Borders$DoubleLine.char[TITLE_RIGHT] = \\u2550\n" +
            "\n" +
            "#Button\n" +
            "com.googlecode.lanterna.gui2.Button.renderer = com.googlecode.lanterna.gui2.Button$DefaultButtonRenderer\n" +
            "com.googlecode.lanterna.gui2.Button.sgr = bold\n" +
            "com.googlecode.lanterna.gui2.Button.foreground[SELECTED] = yellow\n" +
            "com.googlecode.lanterna.gui2.Button.foreground[PRELIGHT] = red\n" +
            "com.googlecode.lanterna.gui2.Button.background[PRELIGHT] = white\n" +
            "com.googlecode.lanterna.gui2.Button.sgr[PRELIGHT] =\n" +
            "com.googlecode.lanterna.gui2.Button.foreground[INSENSITIVE] = black\n" +
            "com.googlecode.lanterna.gui2.Button.background[INSENSITIVE] = white\n" +
            "com.googlecode.lanterna.gui2.Button.char[LEFT_BORDER] = <\n" +
            "com.googlecode.lanterna.gui2.Button.char[RIGHT_BORDER] = >\n" +
            "\n" +
            "# CheckBox\n" +
            "com.googlecode.lanterna.gui2.CheckBox.foreground[INSENSITIVE] = black\n" +
            "com.googlecode.lanterna.gui2.CheckBox.background[INSENSITIVE] = white\n" +
            "com.googlecode.lanterna.gui2.CheckBox.char[MARKER] = x\n" +
            "\n" +
            "# CheckBoxList\n" +
            "com.googlecode.lanterna.gui2.CheckBoxList.foreground[SELECTED] = black\n" +
            "com.googlecode.lanterna.gui2.CheckBoxList.background[SELECTED] = white\n" +
            "com.googlecode.lanterna.gui2.CheckBoxList.sgr[SELECTED] =\n" +
            "com.googlecode.lanterna.gui2.CheckBoxList.char[LEFT_BRACKET] = [\n" +
            "com.googlecode.lanterna.gui2.CheckBoxList.char[RIGHT_BRACKET] = ]\n" +
            "com.googlecode.lanterna.gui2.CheckBoxList.char[MARKER] = x\n" +
            "\n" +
            "# ComboBox\n" +
            "com.googlecode.lanterna.gui2.ComboBox.sgr[PRELIGHT] =\n" +
            "com.googlecode.lanterna.gui2.ComboBox.foreground[INSENSITIVE] = black\n" +
            "com.googlecode.lanterna.gui2.ComboBox.background[INSENSITIVE] = white\n" +
            "com.googlecode.lanterna.gui2.ComboBox.foreground[SELECTED] = black\n" +
            "com.googlecode.lanterna.gui2.ComboBox.background[SELECTED] = white\n" +
            "\n" +
            "# Default color and style for the window decoration renderer\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.foreground[ACTIVE] = black\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.background[ACTIVE] = white\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.sgr[ACTIVE] =\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.foreground[INSENSITIVE] = black\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.background[INSENSITIVE] = white\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.background[PRELIGHT] = white\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.char[HORIZONTAL_LINE] = \\u2500\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.char[VERTICAL_LINE] = \\u2502\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.char[BOTTOM_LEFT_CORNER] = \\u2514\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.char[TOP_LEFT_CORNER] = \\u250c\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.char[BOTTOM_RIGHT_CORNER] = \\u2518\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.char[TOP_RIGHT_CORNER] = \\u2510\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.char[TITLE_SEPARATOR_LEFT] = \\u2500\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.char[TITLE_SEPARATOR_RIGHT] = \\u2500\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.property[TITLE_PADDING] = false\n" +
            "com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer.property[CENTER_TITLE] = false\n" +
            "\n" +
            "# GUI Backdrop\n" +
            "com.googlecode.lanterna.gui2.GUIBackdrop.foreground = cyan\n" +
            "com.googlecode.lanterna.gui2.GUIBackdrop.background = blue\n" +
            "com.googlecode.lanterna.gui2.GUIBackdrop.sgr = bold\n" +
            "\n" +
            "# List boxes default\n" +
            "com.googlecode.lanterna.gui2.AbstractListBox.foreground[INSENSITIVE] = black\n" +
            "com.googlecode.lanterna.gui2.AbstractListBox.background[INSENSITIVE] = white\n" +
            "\n" +
            "# ProgressBar\n" +
            "com.googlecode.lanterna.gui2.ProgressBar.foreground = white\n" +
            "com.googlecode.lanterna.gui2.ProgressBar.background = blue\n" +
            "com.googlecode.lanterna.gui2.ProgressBar.sgr = bold\n" +
            "com.googlecode.lanterna.gui2.ProgressBar.background[ACTIVE] = red\n" +
            "com.googlecode.lanterna.gui2.ProgressBar.foreground[PRELIGHT] = red\n" +
            "com.googlecode.lanterna.gui2.ProgressBar.sgr[PRELIGHT] =\n" +
            "com.googlecode.lanterna.gui2.ProgressBar.char[FILLER] =\n" +
            "\n" +
            "# RadioBoxList\n" +
            "com.googlecode.lanterna.gui2.RadioBoxList.foreground[SELECTED] = black\n" +
            "com.googlecode.lanterna.gui2.RadioBoxList.background[SELECTED] = white\n" +
            "com.googlecode.lanterna.gui2.RadioBoxList.sgr[SELECTED] =\n" +
            "com.googlecode.lanterna.gui2.RadioBoxList.char[LEFT_BRACKET] = <\n" +
            "com.googlecode.lanterna.gui2.RadioBoxList.char[RIGHT_BRACKET] = >\n" +
            "com.googlecode.lanterna.gui2.RadioBoxList.char[MARKER] = o\n" +
            "\n" +
            "# ScrollBar\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[UP_ARROW]=\\u25b2\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[DOWN_ARROW]=\\u25bc\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[LEFT_ARROW]=\\u25c4\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[RIGHT_ARROW]=\\u25ba\n" +
            "\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[VERTICAL_BACKGROUND]=\\u2592\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[VERTICAL_SMALL_TRACKER]=\\u2588\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[VERTICAL_TRACKER_BACKGROUND]=\\u2588\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[VERTICAL_TRACKER_TOP]=\\u2588\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[VERTICAL_TRACKER_BOTTOM]=\\u2588\n" +
            "\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[HORIZONTAL_BACKGROUND]=\\u2592\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[HORIZONTAL_SMALL_TRACKER]=\\u2588\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[HORIZONTAL_TRACKER_BACKGROUND]=\\u2588\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[HORIZONTAL_TRACKER_LEFT]=\\u2588\n" +
            "com.googlecode.lanterna.gui2.ScrollBar.char[HORIZONTAL_TRACKER_RIGHT]=\\u2588\n" +
            "\n" +
            "# Separator\n" +
            "com.googlecode.lanterna.gui2.Separator.sgr = bold\n" +
            "\n" +
            "# Table\n" +
            "com.googlecode.lanterna.gui2.table.Table.sgr[HEADER] = underline,bold\n" +
            "com.googlecode.lanterna.gui2.table.Table.foreground[SELECTED] = black\n" +
            "com.googlecode.lanterna.gui2.table.Table.background[SELECTED] = white\n" +
            "com.googlecode.lanterna.gui2.table.Table.sgr[SELECTED] =\n" +
            "\n" +
            "# TextBox\n" +
            "com.googlecode.lanterna.gui2.TextBox.foreground = white\n" +
            "com.googlecode.lanterna.gui2.TextBox.background = blue\n" +
            "\n" +
            "# Window shadow\n" +
            "com.googlecode.lanterna.gui2.WindowShadowRenderer.background = black\n" +
            "com.googlecode.lanterna.gui2.WindowShadowRenderer.sgr = bold\n" +
            "com.googlecode.lanterna.gui2.WindowShadowRenderer.property[DOUBLE_WIDTH] = true\n" +
            "com.googlecode.lanterna.gui2.WindowShadowRenderer.property[TRANSPARENT] = true";
}
