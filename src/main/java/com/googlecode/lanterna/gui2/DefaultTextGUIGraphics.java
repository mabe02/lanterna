package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.screen.TabBehaviour;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Created by Martin on 2017-08-11.
 */
public class DefaultTextGUIGraphics implements TextGUIGraphics {
    private final TextGUI textGUI;
    private final TextGraphics backend;

    DefaultTextGUIGraphics(TextGUI textGUI, TextGraphics backend) {
        this.backend = backend;
        this.textGUI = textGUI;
    }

    @Override
    public TextGUI getTextGUI() {
        return textGUI;
    }

    @Override
    public DefaultTextGUIGraphics newTextGraphics(TerminalPosition topLeftCorner, TerminalSize size) throws IllegalArgumentException {
        return new DefaultTextGUIGraphics(textGUI, backend.newTextGraphics(topLeftCorner, size));
    }

    @Override
    public DefaultTextGUIGraphics applyThemeStyle(ThemeStyle themeStyle) {
        setForegroundColor(themeStyle.getForeground());
        setBackgroundColor(themeStyle.getBackground());
        setModifiers(themeStyle.getSGRs());
        return this;
    }

    @Override
    public TerminalSize getSize() {
        return backend.getSize();
    }

    @Override
    public TextColor getBackgroundColor() {
        return backend.getBackgroundColor();
    }

    @Override
    public DefaultTextGUIGraphics setBackgroundColor(TextColor backgroundColor) {
        backend.setBackgroundColor(backgroundColor);
        return this;
    }

    @Override
    public TextColor getForegroundColor() {
        return backend.getForegroundColor();
    }

    @Override
    public DefaultTextGUIGraphics setForegroundColor(TextColor foregroundColor) {
        backend.setForegroundColor(foregroundColor);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics enableModifiers(SGR... modifiers) {
        backend.enableModifiers(modifiers);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics disableModifiers(SGR... modifiers) {
        backend.disableModifiers(modifiers);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics setModifiers(EnumSet<SGR> modifiers) {
        backend.setModifiers(modifiers);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics clearModifiers() {
        backend.clearModifiers();
        return this;
    }

    @Override
    public EnumSet<SGR> getActiveModifiers() {
        return backend.getActiveModifiers();
    }

    @Override
    public TabBehaviour getTabBehaviour() {
        return backend.getTabBehaviour();
    }

    @Override
    public DefaultTextGUIGraphics setTabBehaviour(TabBehaviour tabBehaviour) {
        backend.setTabBehaviour(tabBehaviour);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics fill(char c) {
        backend.fill(c);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics fillRectangle(TerminalPosition topLeft, TerminalSize size, char character) {
        backend.fillRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics fillRectangle(TerminalPosition topLeft, TerminalSize size, TextCharacter character) {
        backend.fillRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics drawRectangle(TerminalPosition topLeft, TerminalSize size, char character) {
        backend.drawRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics drawRectangle(TerminalPosition topLeft, TerminalSize size, TextCharacter character) {
        backend.drawRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics fillTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, char character) {
        backend.fillTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics fillTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, TextCharacter character) {
        backend.fillTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics drawTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, char character) {
        backend.drawTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics drawTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, TextCharacter character) {
        backend.drawTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics drawLine(TerminalPosition fromPoint, TerminalPosition toPoint, char character) {
        backend.drawLine(fromPoint, toPoint, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics drawLine(TerminalPosition fromPoint, TerminalPosition toPoint, TextCharacter character) {
        backend.drawLine(fromPoint, toPoint, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics drawLine(int fromX, int fromY, int toX, int toY, char character) {
        backend.drawLine(fromX, fromY, toX, toY, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics drawLine(int fromX, int fromY, int toX, int toY, TextCharacter character) {
        backend.drawLine(fromX, fromY, toX, toY, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics drawImage(TerminalPosition topLeft, TextImage image) {
        backend.drawImage(topLeft, image);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics drawImage(TerminalPosition topLeft, TextImage image, TerminalPosition sourceImageTopLeft, TerminalSize sourceImageSize) {
        backend.drawImage(topLeft, image, sourceImageTopLeft, sourceImageSize);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics setCharacter(TerminalPosition position, char character) {
        backend.setCharacter(position, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics setCharacter(TerminalPosition position, TextCharacter character) {
        backend.setCharacter(position, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics setCharacter(int column, int row, char character) {
        backend.setCharacter(column, row, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics setCharacter(int column, int row, TextCharacter character) {
        backend.setCharacter(column, row, character);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics putString(int column, int row, String string) {
        backend.putString(column, row, string);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics putString(TerminalPosition position, String string) {
        backend.putString(position, string);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics putString(int column, int row, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        backend.putString(column, row, string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics putString(TerminalPosition position, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        backend.putString(position, string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics putString(int column, int row, String string, Collection<SGR> extraModifiers) {
        backend.putString(column, row, string, extraModifiers);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics putCSIStyledString(int column, int row, String string) {
        backend.putCSIStyledString(column, row, string);
        return this;
    }

    @Override
    public DefaultTextGUIGraphics putCSIStyledString(TerminalPosition position, String string) {
        backend.putCSIStyledString(position, string);
        return this;
    }

    @Override
    public TextCharacter getCharacter(int column, int row) {
        return backend.getCharacter(column, row);
    }

    @Override
    public TextCharacter getCharacter(TerminalPosition position) {
        return backend.getCharacter(position);
    }

    @Override
    public DefaultTextGUIGraphics setStyleFrom(StyleSet<?> source) {
        setBackgroundColor(source.getBackgroundColor());
        setForegroundColor(source.getForegroundColor());
        setModifiers(source.getActiveModifiers());
        return this;
    }

}
