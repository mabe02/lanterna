package com.googlecode.lanterna.gui2.textbox;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Extracted from TextBox (Phase 3 refactor — God Class).
 * Bounded undo/redo history of text states.
 */
public class TextBoxUndoStack {
    private final int limit;
    private final Deque<String> undoStack = new ArrayDeque<>();
    private final Deque<String> redoStack = new ArrayDeque<>();

    public TextBoxUndoStack(int limit) {
        this.limit = Math.max(1, limit);
    }

    public void recordChange(String previousState) {
        if (previousState == null) {
            return;
        }
        if (undoStack.size() >= limit) {
            undoStack.removeFirst();
        }
        undoStack.push(previousState);
        redoStack.clear();
    }

    public String popUndo() {
        if (undoStack.isEmpty()) {
            return null;
        }
        String state = undoStack.pop();
        redoStack.push(state);
        return state;
    }

    public String popRedo() {
        if (redoStack.isEmpty()) {
            return null;
        }
        String state = redoStack.pop();
        undoStack.push(state);
        return state;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}