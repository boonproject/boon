package org.boon.expression;

import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.MapObjectConversion;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.boon.Exceptions.die;

/**
 * Created by Richard on 2/10/14.
 */
public class BasicContext implements ObjectContext, Map {

    Object root;

    public BasicContext( Object root ) {
        this.root = root;
    }

    @Override
    public char idxChar( String property ) {
         return BeanUtils.idxChar( root, property );
    }

    @Override
    public byte idxByte( String property ) {

        return BeanUtils.idxByte( root, property );
    }

    @Override
    public short idxShort( String property ) {

        return BeanUtils.idxShort( root, property );
    }

    @Override
    public String idxString( String property ) {

        return (String)BeanUtils.idx( root, property );
    }

    @Override
    public int idxInt( String property ) {
        return BeanUtils.idxInt( root, property );
    }

    @Override
    public float idxFloat( String property ) {
        return BeanUtils.idxFloat( root, property );
    }

    @Override
    public double idxDouble( String property ) {
        return BeanUtils.idxDouble( root, property );
    }

    @Override
    public long idxLong( String property ) {
        return BeanUtils.idxLong( root, property );
    }

    @Override
    public Object idx( String property ) {
        return BeanUtils.idx( root, property );
    }

    @Override
    public <T> T idx( Class<T> type, String property ) {
        return BeanUtils.idx(type, root, property );
    }

    @Override
    public int size() {
       return MapObjectConversion.toMap( root ).size();
    }

    @Override
    public boolean isEmpty() {
        return MapObjectConversion.toMap( root ).isEmpty();
    }

    @Override
    public boolean containsKey( Object key ) {

        //TODO not a good idea
        return MapObjectConversion.toMap( root ).containsKey(key);
    }

    @Override
    public boolean containsValue( Object value ) {


        //TODO not a good idea
        return MapObjectConversion.toMap( root ).containsValue( value );
    }

    @Override
    public Object get( Object key ) {

        return this.idx((key.toString()));

    }

    @Override
    public Object put( Object key, Object value ) {
        return die();
    }

    @Override
    public Object remove( Object key ) {
        return die();
    }

    @Override
    public void putAll( Map m ) {
         die();

    }

    @Override
    public void clear() {
        die();
    }

    @Override
    public Set keySet() {
        return die(Set.class, "Context not map");
    }

    @Override
    public Collection values() {

        return die(Set.class, "Context not map");
    }

    @Override
    public Set<Entry> entrySet() {

        return die(Set.class, "Context not map");
    }
}
