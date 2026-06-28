package com.googlecode.lanterna.gui2.textbox;

/**
 * Extracted from TextBox (Phase 3 refactor — God Class).
 * Manages text selection state: start, end, and querying the selection.
 */
public class TextBoxSelectionModel {
    private int selectionStart = -1;
    private int selectionEnd = -1;
    private boolean selectionActive = false;

    public void clearSelection() {
        selectionStart = -1;
        selectionEnd = -1;
        selectionActive = false;
    }

    public void setSelection(int start, int end) {
        this.selectionStart = Math.min(start, end);
        this.selectionEnd = Math.max(start, end);
        this.selectionActive = (selectionStart != selectionEnd);
    }

    public boolean isSelectionActive() {
        return selectionActive;
    }

    public int getSelectionStart() {
        return selectionStart;
    }

    public int getSelectionEnd() {
        return selectionEnd;
    }

    public String extractSelected(String source) {
        if (!selectionActive || source == null) {
            return "";
        }
        int start = Math.max(0, Math.min(selectionStart, source.length()));
        int end = Math.max(0, Math.min(selectionEnd, source.length()));
        if (end <= start) {
            return "";
        }
        return source.substring(start, end);
    }
}