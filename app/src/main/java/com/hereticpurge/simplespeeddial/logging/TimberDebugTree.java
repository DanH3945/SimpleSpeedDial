package com.hereticpurge.simplespeeddial.logging;

import timber.log.Timber;

public class TimberDebugTree extends Timber.DebugTree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

        // Modify the Timber Debug tree messages to be a bit more noticeable.

        StringBuilder sb = new StringBuilder();

        String filler = " ---------- ";

        sb.append(filler);
        sb.append(message);
        sb.append(filler);

        String newMessage = sb.toString();

        super.log(priority, tag, newMessage, t);
    }
}
