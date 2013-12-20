package org.boon.core;


/**
 * <p>
 * Make objects nameable.
 * <small>
 * <p/>
 * </small>
 * </p>
 *
 * @author Rick Hightower
 */
public interface NameAware {
    void setName ( String name );

    String getName ();

    void init ();

}
