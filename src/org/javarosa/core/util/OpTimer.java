package org.javarosa.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpTimer {
    private static final Logger logger = LoggerFactory.getLogger(OpTimer.class.getSimpleName());
    private static int level = 0;
    private static boolean enabled = true /* todo disable */;
    private final long startTime;
    private final int curLevel;
    private final String op;

    public OpTimer(String op) {
        this.startTime = System.nanoTime();
        this.curLevel = ++level;
        this.op = op;
        if (enabled)
            logger.debug(String.format("Starting%s%s", getTabs(), op));
    }

    public void end() {
        if (enabled)
            logger.debug(String.format("Finished%s%s\t%.1f", getTabs(), op, (System.nanoTime() - startTime) / 1e6));
        --level;
    }

    public void end(String extraMessage) {
        if (enabled)
            logger.debug(String.format("%s%s", getTabs(), extraMessage));
        end();
    }

    private String getTabs() {
        StringBuilder tabs = new StringBuilder();
        for (int l = 0; l < curLevel; ++l) tabs.append("\t");
        return tabs.toString();
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        OpTimer.enabled = enabled;
    }
}
