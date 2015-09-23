package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;

/**
 * Classic scrollbar that can be used to display where inside a larger component a view is showing. This implementation
 * is not interactable and needs to be driven externally.
 *
 * @author Martin
 */
public class ScrollBar extends AbstractComponent<ScrollBar> {

    private final Direction direction;
    private int maximum;
    private int position;
    private int viewSize;

    public ScrollBar(Direction direction) {
        this.direction = direction;
        this.maximum = 100;
        this.position = 0;
        this.viewSize = 0;
    }

    public Direction getDirection() {
        return direction;
    }

    public ScrollBar setScrollMaximum(int maximum) {
        if(maximum < 0) {
            throw new IllegalArgumentException("Cannot set ScrollBar maximum to " + maximum);
        }
        this.maximum = maximum;
        invalidate();
        return this;
    }

    public int getScrollMaximum() {
        return maximum;
    }

    public ScrollBar setScrollPosition(int position) {
        this.position = Math.min(position, this.maximum);
        invalidate();
        return this;
    }

    public int getScrollPosition() {
        return position;
    }

    public void setViewSize(int viewSize) {
        this.viewSize = viewSize;
    }

    public int getViewSize() {
        if(viewSize > 0) {
            return viewSize;
        }
        if(direction == Direction.HORIZONTAL) {
            return getSize().getColumns();
        }
        else {
            return getSize().getRows();
        }
    }

    @Override
    protected ComponentRenderer<ScrollBar> createDefaultRenderer() {
        return new ClassicScrollBarRenderer();
    }

    public static abstract class ScrollBarRenderer implements ComponentRenderer<ScrollBar> {
        @Override
        public TerminalSize getPreferredSize(ScrollBar component) {
            return TerminalSize.ONE;
        }
    }

    public static class ClassicScrollBarRenderer extends ScrollBarRenderer {

        private boolean growScrollTracker;

        public ClassicScrollBarRenderer() {
            this.growScrollTracker = true;
        }

        public void setGrowScrollTracker(boolean growScrollTracker) {
            this.growScrollTracker = growScrollTracker;
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, ScrollBar component) {
            TerminalSize size = graphics.getSize();
            Direction direction = component.getDirection();
            int position = component.getScrollPosition();
            int maximum = component.getScrollMaximum();
            int viewSize = component.getViewSize();

            if(size.getRows() == 0 || size.getColumns() == 0) {
                return;
            }

            //Adjust position if necessary
            if(position + viewSize >= maximum) {
                position = Math.max(0, maximum - viewSize);
                component.setScrollPosition(position);
            }

            ThemeDefinition themeDefinition = graphics.getThemeDefinition(ScrollBar.class);
            graphics.applyThemeStyle(themeDefinition.getNormal());

            if(direction == Direction.VERTICAL) {
                if(size.getRows() == 1) {
                    graphics.setCharacter(0, 0, themeDefinition.getCharacter("VERTICAL_BACKGROUND", Symbols.BLOCK_MIDDLE));
                }
                else if(size.getRows() == 2) {
                    graphics.setCharacter(0, 0, themeDefinition.getCharacter("UP_ARROW", Symbols.ARROW_UP));
                    graphics.setCharacter(0, 1, themeDefinition.getCharacter("DOWN_ARROW", Symbols.ARROW_DOWN));
                }
                else {
                    int scrollableArea = size.getRows() - 2;
                    int scrollTrackerSize = 1;
                    if(growScrollTracker) {
                        float ratio = clampRatio((float) viewSize / (float) maximum);
                        scrollTrackerSize = Math.max(1, (int) (ratio * (float) scrollableArea));
                    }

                    float ratio = clampRatio((float)position / (float)(maximum - viewSize));
                    int scrollTrackerPosition = (int)(ratio * (float)(scrollableArea - scrollTrackerSize)) + 1;

                    graphics.setCharacter(0, 0, themeDefinition.getCharacter("UP_ARROW", Symbols.ARROW_UP));
                    graphics.drawLine(0, 1, 0, size.getRows() - 2, themeDefinition.getCharacter("VERTICAL_BACKGROUND", Symbols.BLOCK_MIDDLE));
                    graphics.setCharacter(0, size.getRows() - 1, themeDefinition.getCharacter("DOWN_ARROW", Symbols.ARROW_DOWN));
                    if(scrollTrackerSize == 1) {
                        graphics.setCharacter(0, scrollTrackerPosition, themeDefinition.getCharacter("VERTICAL_SMALL_TRACKER", Symbols.SOLID_SQUARE_SMALL));
                    }
                    else if(scrollTrackerSize == 2) {
                        graphics.setCharacter(0, scrollTrackerPosition, themeDefinition.getCharacter("VERTICAL_TRACKER_TOP", (char)0x28c));
                        graphics.setCharacter(0, scrollTrackerPosition + 1, themeDefinition.getCharacter("VERTICAL_TRACKER_BOTTOM", 'v'));
                    }
                    else {
                        graphics.setCharacter(0, scrollTrackerPosition, themeDefinition.getCharacter("VERTICAL_TRACKER_TOP", (char)0x28c));
                        graphics.drawLine(0, scrollTrackerPosition + 1, 0, scrollTrackerPosition + scrollTrackerSize - 2, themeDefinition.getCharacter("VERTICAL_TRACKER_BACKGROUND", ' '));
                        graphics.setCharacter(0, scrollTrackerPosition + (scrollTrackerSize / 2), themeDefinition.getCharacter("VERTICAL_SMALL_TRACKER", Symbols.SOLID_SQUARE_SMALL));
                        graphics.setCharacter(0, scrollTrackerPosition + scrollTrackerSize - 1, themeDefinition.getCharacter("VERTICAL_TRACKER_BOTTOM", 'v'));
                    }
                }
            }
            else {
                if(size.getColumns() == 1) {
                    graphics.setCharacter(0, 0, themeDefinition.getCharacter("HORIZONTAL_BACKGROUND", Symbols.BLOCK_MIDDLE));
                }
                else if(size.getColumns() == 2) {
                    graphics.setCharacter(0, 0, Symbols.ARROW_LEFT);
                    graphics.setCharacter(1, 0, Symbols.ARROW_RIGHT);
                }
                else {
                    int scrollableArea = size.getColumns() - 2;
                    int scrollTrackerSize = 1;
                    if(growScrollTracker) {
                        float ratio = clampRatio((float) viewSize / (float) maximum);
                        scrollTrackerSize = Math.max(1, (int) (ratio * (float) scrollableArea));
                    }

                    float ratio = clampRatio((float)position / (float)(maximum - viewSize));
                    int scrollTrackerPosition = (int)(ratio * (float)(scrollableArea - scrollTrackerSize)) + 1;

                    graphics.setCharacter(0, 0, themeDefinition.getCharacter("LEFT_ARROW", Symbols.ARROW_LEFT));
                    graphics.drawLine(1, 0, size.getColumns() - 2, 0, themeDefinition.getCharacter("HORIZONTAL_BACKGROUND", Symbols.BLOCK_MIDDLE));
                    graphics.setCharacter(size.getColumns() - 1, 0, themeDefinition.getCharacter("RIGHT_ARROW", Symbols.ARROW_RIGHT));
                    if(scrollTrackerSize == 1) {
                        graphics.setCharacter(scrollTrackerPosition, 0, themeDefinition.getCharacter("HORIZONTAL_SMALL_TRACKER", Symbols.SOLID_SQUARE_SMALL));
                    }
                    else if(scrollTrackerSize == 2) {
                        graphics.setCharacter(scrollTrackerPosition, 0, themeDefinition.getCharacter("HORIZONTAL_TRACKER_LEFT", '<'));
                        graphics.setCharacter(scrollTrackerPosition + 1, 0, themeDefinition.getCharacter("HORIZONTAL_TRACKER_RIGHT", '>'));
                    }
                    else {
                        graphics.setCharacter(scrollTrackerPosition, 0, themeDefinition.getCharacter("HORIZONTAL_TRACKER_LEFT", '<'));
                        graphics.drawLine(scrollTrackerPosition + 1, 0, scrollTrackerPosition + scrollTrackerSize - 2, 0, themeDefinition.getCharacter("HORIZONTAL_TRACKER_BACKGROUND", ' '));
                        graphics.setCharacter(scrollTrackerPosition + (scrollTrackerSize / 2), 0, themeDefinition.getCharacter("HORIZONTAL_SMALL_TRACKER", Symbols.SOLID_SQUARE_SMALL));
                        graphics.setCharacter(scrollTrackerPosition + scrollTrackerSize - 1, 0, themeDefinition.getCharacter("HORIZONTAL_TRACKER_RIGHT", '>'));
                    }
                }
            }
        }

        private float clampRatio(float value) {
            if(value < 0.0f) {
                return 0.0f;
            }
            else if(value > 1.0f) {
                return 1.0f;
            }
            else {
                return value;
            }
        }
    }
}
