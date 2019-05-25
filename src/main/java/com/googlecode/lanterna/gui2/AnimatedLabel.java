/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 * 
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * This is a special label that contains not just a single text to display but a number of frames that are cycled
 * through. The class will manage a timer on its own and ensure the label is updated and redrawn. There is a static
 * helper method available to create the classic "spinning bar": {@code createClassicSpinningLine()}
 */
public class AnimatedLabel extends Label {
    private static Timer TIMER = null;
    private static final WeakHashMap<AnimatedLabel, TimerTask> SCHEDULED_TASKS = new WeakHashMap<AnimatedLabel, TimerTask>();

    /**
     * Creates a classic spinning bar which can be used to signal to the user that an operation in is process.
     * @return {@code AnimatedLabel} instance which is setup to show a spinning bar
     */
    public static AnimatedLabel createClassicSpinningLine() {
        return createClassicSpinningLine(150);
    }

    /**
     * Creates a classic spinning bar which can be used to signal to the user that an operation in is process.
     * @param speed Delay in between each frame
     * @return {@code AnimatedLabel} instance which is setup to show a spinning bar
     */
    public static AnimatedLabel createClassicSpinningLine(int speed) {
        AnimatedLabel animatedLabel = new AnimatedLabel("-");
        animatedLabel.addFrame("\\");
        animatedLabel.addFrame("|");
        animatedLabel.addFrame("/");
        animatedLabel.startAnimation(speed);
        return animatedLabel;
    }

    private final List<String[]> frames;
    private TerminalSize combinedMaximumPreferredSize;
    private int currentFrame;

    /**
     * Creates a new animated label, initially set to one frame. You will need to add more frames and call
     * {@code startAnimation()} for this to start moving.
     *
     * @param firstFrameText The content of the label at the first frame
     */
    public AnimatedLabel(String firstFrameText) {
        super(firstFrameText);
        frames = new ArrayList<String[]>();
        currentFrame = 0;
        combinedMaximumPreferredSize = TerminalSize.ZERO;

        String[] lines = splitIntoMultipleLines(firstFrameText);
        frames.add(lines);
        ensurePreferredSize(lines);
    }

    @Override
    protected synchronized TerminalSize calculatePreferredSize() {
        return super.calculatePreferredSize().max(combinedMaximumPreferredSize);
    }

    /**
     * Adds one more frame at the end of the list of frames
     * @param text Text to use for the label at this frame
     * @return Itself
     */
    public synchronized AnimatedLabel addFrame(String text) {
        String[] lines = splitIntoMultipleLines(text);
        frames.add(lines);
        ensurePreferredSize(lines);
        return this;
    }

    private void ensurePreferredSize(String[] lines) {
        combinedMaximumPreferredSize = combinedMaximumPreferredSize.max(getBounds(lines, combinedMaximumPreferredSize));
    }

    /**
     * Advances the animated label to the next frame. You normally don't need to call this manually as it will be done
     * by the animation thread.
     */
    public synchronized void nextFrame() {
        currentFrame++;
        if(currentFrame >= frames.size()) {
            currentFrame = 0;
        }
        super.setLines(frames.get(currentFrame));
        invalidate();
    }

    @Override
    public void onRemoved(Container container) {
        stopAnimation();
    }

    /**
     * Starts the animation thread which will periodically call {@code nextFrame()} at the interval specified by the
     * {@code millisecondsPerFrame} parameter. After all frames have been cycled through, it will start over from the
     * first frame again.
     * @param millisecondsPerFrame The interval in between every frame
     * @return Itself
     */
    public synchronized AnimatedLabel startAnimation(long millisecondsPerFrame) {
        if(TIMER == null) {
            TIMER = new Timer("AnimatedLabel");
        }
        AnimationTimerTask animationTimerTask = new AnimationTimerTask(this);
        SCHEDULED_TASKS.put(this, animationTimerTask);
        TIMER.scheduleAtFixedRate(animationTimerTask, millisecondsPerFrame, millisecondsPerFrame);
        return this;
    }

    /**
     * Halts the animation thread and the label will stop at whatever was the current frame at the time when this was
     * called
     * @return Itself
     */
    public synchronized AnimatedLabel stopAnimation() {
        removeTaskFromTimer(this);
        return this;
    }

    private static synchronized void removeTaskFromTimer(AnimatedLabel animatedLabel) {
        SCHEDULED_TASKS.get(animatedLabel).cancel();
        SCHEDULED_TASKS.remove(animatedLabel);
        canCloseTimer();
    }

    private static synchronized void canCloseTimer() {
        if(SCHEDULED_TASKS.isEmpty()) {
            TIMER.cancel();
            TIMER = null;
        }
    }

    private static class AnimationTimerTask extends TimerTask {
        private final WeakReference<AnimatedLabel> labelRef;

        private AnimationTimerTask(AnimatedLabel label) {
            this.labelRef = new WeakReference<AnimatedLabel>(label);
        }

        @Override
        public void run() {
            AnimatedLabel animatedLabel = labelRef.get();
            if(animatedLabel == null) {
                cancel();
                canCloseTimer();
            }
            else {
                if(animatedLabel.getBasePane() == null) {
                    animatedLabel.stopAnimation();
                }
                else {
                    animatedLabel.nextFrame();
                }
            }
        }
    }
}
