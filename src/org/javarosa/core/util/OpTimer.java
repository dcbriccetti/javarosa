package org.javarosa.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpTimer {
    private static final Logger logger = LoggerFactory.getLogger(OpTimer.class.getSimpleName());
    private static int level = 0;
    private final long startTime;
    private final int curLevel;
    private final String op;

    private OpTimer(int curLevel, String op) {
        this.startTime = System.nanoTime();
        this.curLevel = curLevel;
        this.op = op;
        logger.debug(String.format("Starting%s%s", getTabs(), op));
    }

    public static OpTimer begin(String op) {
        return new OpTimer(++level, op);
    }

    public void end() {
        logger.debug(String.format("Finished%s%s\t%.1f", getTabs(), op, (System.nanoTime() - startTime) / 1e6));
        --level;
    }

    private String getTabs() {
        StringBuilder tabs = new StringBuilder();
        for (int l = 0; l < curLevel; ++l) tabs.append("\t");
        return tabs.toString();
    }
}
