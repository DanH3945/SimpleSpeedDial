package com.hereticpurge.simplespeeddial.logging;

import timber.log.Timber;

public class TimberDebugTree extends Timber.DebugTree {

    private final String FILLER = " ---------- ";
    private final String TIMBER = "TIMBER";

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

        // Modify the Timber Debug tree messages to be a bit more noticeable.

        StringBuilder sb = new StringBuilder();

        sb.append(TIMBER);
        sb.append(FILLER);
        sb.append(message);
        sb.append(FILLER);

        String newMessage = sb.toString();

        super.log(priority, tag, newMessage, t);
    }
}
