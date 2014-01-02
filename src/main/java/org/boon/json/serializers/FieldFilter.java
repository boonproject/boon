package org.boon.json.serializers;

import org.boon.core.reflection.fields.FieldAccess;

/**
 * Created by rick on 1/2/14.
 */
public interface FieldFilter {
    boolean include (Object parent, FieldAccess fieldAccess);
}
