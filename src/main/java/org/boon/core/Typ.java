package org.boon.core;


import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class Typ {

    /* Core */
    public static final Class<Object> object = Object.class;
    public static final Class<String> string = String.class;
    public static final Class<List> list = List.class;
    public static final Class<CharSequence> chars = CharSequence.class;
    public static final Class<Set> set = Set.class;
    public static final Class<Collection> collection = Collection.class;


    public static final Class<Comparable>  comparable = Comparable.class;
    /* Wrapper */
    public static final Class<Boolean> bool          = Boolean.class;
    public static final Class<Integer> integer       = Integer.class;
    public static final Class<Long> longWrapper      = Long.class;
    public static final Class<Double> doubleWrapper  = Double.class;
    public static final Class<Float> floatWrapper    = Float.class;
    public static final Class<Number> number = Number.class;


    /* primitive */
    public static final Class<?> flt    = float.class;
    public static final Class<?> lng    = long.class;
    public static final Class<?> dbl    = double.class;
    public static final Class<?> intgr  = int.class;
    public static final Class<?> bln    = boolean.class;
    public static final Class<?> shrt   = short.class;
    public static final Class<?> chr    = char.class;
    public static final Class<?> bt     = byte.class;


    /* Utility */
    public static final Class<Date> date = Date.class;
    public static final Class<Calendar> calendar = Calendar.class;
    public static final Class<File> file = File.class;
    public static final Class<Path> path = Path.class;


    /* Arrays. */
    public static final Class<String[]>  stringArray = String[].class;
    public static final Class<int[]>     intArray = int[].class;
    public static final Class<byte[]>    byteArray = byte[].class;
    public static final Class<short[]>   shortArray = short[].class;
    public static final Class<char[]>    charArray = char[].class;
    public static final Class<long[]>    longArray = long[].class;
    public static final Class<float[]>   floatArray = float[].class;
    public static final Class<double[]>  doubleArray = double[].class;
    public static final Class<Object[]>  objectArray = Object[].class;

}
