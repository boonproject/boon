package org.boon.handlebars;

import org.boon.primitive.CharBuf;

/**
 * Created by Richard on 2/27/14.
 */
public interface Command {

    void processCommand(CharBuf output, String arguments, CharSequence block, Object context);
}
