package org.boon.template;

import org.boon.core.reflection.BeanUtils;
import org.boon.primitive.CharBuf;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Richard on 9/15/14.
 */
public class JSTLTemplate implements Template {


    @Override
    public String replace(String string, Object context) {
        CharBuf buf = CharBuf.create(string.length());
        JSTLCoreParser parser = new JSTLCoreParser();
        parser.parse(string);

        Iterator<JSTLCoreParser.Token> tokens = parser.getTokenList().iterator();

        while (tokens.hasNext()) {
            final JSTLCoreParser.Token token = tokens.next();
            switch (token.type()) {
                case TEXT:
                    buf.add(string, token.start(), token.stop());
                    break;
                case EXPRESSION:
                    String path = string.substring(token.start(), token.stop());
                    buf.add(BeanUtils.idxStr(context, path));
                    break;
                case COMMAND:
                    handleCommand(token, tokens);
                    break;

            }
        }

        return buf.toString();
    }

    private void handleCommand(JSTLCoreParser.Token token, Iterator<JSTLCoreParser.Token> tokens) {

    }
}
