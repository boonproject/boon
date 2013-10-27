package org.boon.datarepo.modification;


public interface ModificationListener<KEY, ITEM> {

    void modification(ModificationEvent event);

}
