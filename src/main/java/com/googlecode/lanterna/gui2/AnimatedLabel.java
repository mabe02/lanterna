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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Special kind of label that cycles through a list of texts
 */
public class AnimatedLabel extends Label {
    private static Timer TIMER = null;
    private static final WeakHashMap<AnimatedLabel, TimerTask> SCHEDULED_TASKS = new WeakHashMap<AnimatedLabel, TimerTask>();

    public static AnimatedLabel createClassicSpinningLine() {
        return createClassicSpinningLine(150);
    }

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

    public AnimatedLabel(String firstFrameText) {
        super(firstFrameText);
        frames = new ArrayList<String[]>();
        currentFrame = 0;
        combinedMaximumPreferredSize = TerminalSize.ZERO;

        String[] lines = splitIntoMultipleLines(firstFrameText);
        frames.add(lines);
        ensurePreferredSize(lines);
    }

    public void addFrame(String text) {
        String[] lines = splitIntoMultipleLines(text);
        frames.add(lines);
        ensurePreferredSize(lines);
    }

    private void ensurePreferredSize(String[] lines) {
        combinedMaximumPreferredSize = combinedMaximumPreferredSize.max(getBounds(lines, combinedMaximumPreferredSize));
    }

    public void nextFrame() {
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

    public synchronized void startAnimation(long millisecondsPerFrame) {
        if(TIMER == null) {
            TIMER = new Timer("AnimatedLabel");
        }
        AnimationTimerTask animationTimerTask = new AnimationTimerTask(this);
        SCHEDULED_TASKS.put(this, animationTimerTask);
        TIMER.scheduleAtFixedRate(animationTimerTask, millisecondsPerFrame, millisecondsPerFrame);
    }

    public void stopAnimation() {
        removeTaskFromTimer(this);
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
