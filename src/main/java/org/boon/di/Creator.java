package org.boon.di;

import java.util.Map;

/**
 * Created by Richard on 2/4/14.
 */
public class Creator {

    static <T> T create(Class<T> type, Map<?, ?> map) {
      Context context = ContextFactory.fromMap(map);
      return context.get( type );
    }
}
