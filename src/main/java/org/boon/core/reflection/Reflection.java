package org.boon.core.reflection;

import org.boon.Exceptions;
import org.boon.Lists;
import org.boon.Maps;
import org.boon.Sets;
import org.boon.core.Typ;
import org.boon.core.reflection.fields.*;

import org.boon.core.reflection.Conversions;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


import static java.util.logging.Level.WARNING;
import static org.boon.Exceptions.die;
import static org.boon.Str.lines;


import static org.boon.Str.lower;
import static org.boon.Str.slc;


public class Reflection {

    private static final Logger log = Logger.getLogger(Reflection.class.getName());

    private static boolean _useUnsafe;

    static Set<String> fieldSortNames = Sets.set("name", "orderBy", "title", "key");
    static Set<String> fieldSortNamesPrefixes = Sets.set("Name", "Title", "Key");

    static ConcurrentHashMap<Class, String> sortableFields = new ConcurrentHashMap<>();


    static {
        try {
            Class.forName("sun.misc.Unsafe");
            _useUnsafe = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            _useUnsafe = false;
        }

        _useUnsafe = _useUnsafe && ! Boolean.getBoolean("com.org.org.boon.noUnsafe");
        _useUnsafe = true;
    }


    /**
     * Does this return getPropertyFieldFieldAccessMap(clazz, true, true);
     *
     * @param clazz
     * @return
     */
    public static Map<String, FieldAccess> getPropertyFieldAccessMap(Class<?> clazz) {
        return getPropertyFieldAccessMap(clazz, true, true);
    }

    /**
     * Gets a list of fields merges with properties if field is not found.
     *
     * @param clazz
     * @param useFieldFirst
     * @param useUnSafe
     * @return
     */
    public static Map<String, FieldAccess> getPropertyFieldAccessMap(Class<?> clazz, boolean useFieldFirst, boolean useUnSafe) {
        Map<String, FieldAccess> fieldsFallbacks = null;
        Map<String, FieldAccess> fieldsPrimary = null;

        if (useFieldFirst) {
            fieldsPrimary = Reflection.getAllAccessorFields(clazz, useUnSafe);

            fieldsFallbacks = Reflection.getPropertyFieldAccessors(clazz);

        } else {

            fieldsFallbacks = Reflection.getAllAccessorFields(clazz, useUnSafe);

            fieldsPrimary = Reflection.getPropertyFieldAccessors(clazz);

        }

        /* Add missing fields */
        for (Map.Entry<String, FieldAccess> field : fieldsFallbacks.entrySet()) {
            if (!fieldsPrimary.containsKey(field.getKey())) {
                fieldsPrimary.put(field.getKey(), field.getValue());
            }
        }

        return fieldsPrimary;
    }


    public static String getFirstStringFieldName(Object value1, String name) {
        List<Field> fields = getAllFields(value1.getClass());
        for (Field field : fields) {
            if (field.getName().equals(name) || field.getType().equals(Typ.string) && !Modifier.isStatic(field.getModifiers())
                    && field.getDeclaringClass() == value1.getClass()) {
                return field.getName();
            }
        }
        for (Field field : fields) {
            if (field.getName().equals(name) || field.getType().equals(Typ.string) && !Modifier.isStatic(field.getModifiers())
                    ) {
                return field.getName();
            }
        }

        return null;
    }

    public static String getFirstComparableOrPrimitive(Object value1) {
        List<Field> fields = getAllFields(value1.getClass());
        for (Field field : fields) {

            if ((field.getType().isPrimitive() || Conversions.isComparable(field.getType())
                    && !Modifier.isStatic(field.getModifiers())
                    && field.getDeclaringClass() == value1.getClass())
                    ) {
                return field.getName();
            }
        }
        for (Field field : fields) {

            if ((field.getType().isPrimitive() || Conversions.isComparable(field.getType())
                    && !Modifier.isStatic(field.getModifiers())
            )
                    ) {
                return field.getName();
            }
        }

        return null;
    }

    public static String getFirstStringFieldNameEndsWith(Object value1, String name) {
        List<Field> fields = getAllFields(value1.getClass());
        for (Field field : fields) {
            if (field.getName().endsWith(name) || field.getType().equals(Typ.string) && !Modifier.isStatic(field.getModifiers())
                    && field.getDeclaringClass() == value1.getClass()) {
                return field.getName();
            }
        }
        for (Field field : fields) {
            if (field.getName().endsWith(name) || field.getType().equals(Typ.string) && !Modifier.isStatic(field.getModifiers())
                    ) {
                return field.getName();
            }
        }

        return null;
    }




    public static String getSortableField(Object value1) {

        String fieldName = sortableFields.get(value1.getClass());
        if (fieldName == null) {
            for (String name : fieldSortNames) {
                fieldName = getFirstStringFieldName(value1, name);
                if (fieldName != null) {
                    break;
                }
            }
            if (fieldName == null) {
                for (String name : fieldSortNamesPrefixes) {
                    fieldName = getFirstStringFieldNameEndsWith(value1, name);
                    if (fieldName != null) {
                        break;
                    }
                }
            }

            if (fieldName == null) {
                fieldName = getFirstComparableOrPrimitive(value1);
            }

            if (fieldName == null) {
                die("Could not find a sortable field for type " + value1.getClass());
            }
            sortableFields.put(Typ.object.getClass(), fieldName);
        }
        return fieldName;

    }

    public static Map<String, FieldAccess> getFieldsFromObject(Object item) {
        Map<String, FieldAccess> fields = null;

        fields = Reflection.getPropertyFieldAccessMap(item.getClass());

        if (item instanceof Map) {
            fields = Reflection.getFieldsFromMap(fields, (Map<String, Object>) item);
        }
        return fields;

    }

    private static Map<String, FieldAccess> getFieldsFromMap(Map<String, FieldAccess> fields, Map<String, Object> map) {

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            fields.put(entry.getKey(), new MapField(entry.getKey()));
        }
        return fields;

    }

    public static Object getPropertyValue(Object object, String... properties) {
        for (String property : properties) {
            Map<String, FieldAccess> fields = Reflection.getPropertyFieldAccessMap(object.getClass());
            object = fields.get(property);
        }
        return object;
    }


    public static int getPropertyInt(Object object, String... properties) {
        Map<String, FieldAccess> fields = Reflection.getPropertyFieldAccessMap(object.getClass());
        for (int index = 0; index < properties.length - 1; index++) {
            fields = Reflection.getPropertyFieldAccessMap(object.getClass());
            object = fields.get(properties[index]);
        }
        FieldAccess field = fields.get(properties[properties.length - 1]);
        if (field.getType() == Typ.intgr) {
            return field.getInt(object);
        } else {
            return Conversions.toInt(field.getValue(object));
        }
    }


    public static byte getPropertyByte(Object object, String... properties) {
        Map<String, FieldAccess> fields = null;
        for (int index = 0; index < properties.length - 1; index++) {
            fields = Reflection.getPropertyFieldAccessMap(object.getClass());
            object = fields.get(properties[index]);
        }
        FieldAccess field = fields.get(properties[properties.length - 1]);
        if (field.getType() == Typ.bt) {
            return field.getByte(object);
        } else {
            return Conversions.toByte(field.getValue(object));
        }
    }

    public static float getPropertyFloat(Object object, String... properties) {
        Map<String, FieldAccess> fields = null;
        for (int index = 0; index < properties.length - 1; index++) {
            fields = Reflection.getPropertyFieldAccessMap(object.getClass());
            object = fields.get(properties[index]);
        }
        FieldAccess field = fields.get(properties[properties.length - 1]);
        if (field.getType() == Typ.flt) {
            return field.getFloat(object);
        } else {
            return Conversions.toFloat(field.getValue(object));
        }
    }


    public static short getPropertyShort(Object object, String... properties) {
        Map<String, FieldAccess> fields = null;
        for (int index = 0; index < properties.length - 1; index++) {
            fields = Reflection.getPropertyFieldAccessMap(object.getClass());
            object = fields.get(properties[index]);
        }
        FieldAccess field = fields.get(properties[properties.length - 1]);
        if (field.getType() == Typ.shrt) {
            return field.getShort(object);
        } else {
            return Conversions.toShort(field.getValue(object));
        }
    }


    public static char getPropertyChar(Object object, String... properties) {
        Map<String, FieldAccess> fields = null;
        for (int index = 0; index < properties.length - 1; index++) {
            fields = Reflection.getPropertyFieldAccessMap(object.getClass());
            object = fields.get(properties[index]);
        }
        FieldAccess field = fields.get(properties[properties.length - 1]);
        if (field.getType() == Typ.chr) {
            return field.getChar(object);
        } else {
            return Conversions.toChar(field.getValue(object));
        }
    }


    public static double getPropertyDouble(Object object, String... properties) {
        Map<String, FieldAccess> fields = null;
        for (int index = 0; index < properties.length - 1; index++) {
            fields = Reflection.getPropertyFieldAccessMap(object.getClass());
            object = fields.get(properties[index]);
        }
        FieldAccess field = fields.get(properties[properties.length - 1]);
        if (field.getType() == Typ.dbl) {
            return field.getDouble(object);
        } else {
            return Conversions.toDouble(field.getValue(object));
        }
    }


    public static long getPropertyLong(Object object, String... properties) {
        Map<String, FieldAccess> fields = null;
        for (int index = 0; index < properties.length - 1; index++) {
            fields = Reflection.getPropertyFieldAccessMap(object.getClass());
            object = fields.get(properties[index]);
        }
        FieldAccess field = fields.get(properties[properties.length - 1]);
        if (field.getType() == Typ.lng) {
            return field.getLong(object);
        } else {
            return Conversions.toLong(field.getValue(object));
        }
    }

    public static boolean hasField(Class<?> aClass, String name) {
        Map<String, FieldAccess> fields = getAllAccessorFields(aClass);
        return fields.containsKey(name);
    }

    @SuppressWarnings("serial")
    public static class ReflectionException extends RuntimeException {

        public ReflectionException() {
            super();
        }

        public ReflectionException(String message, Throwable cause) {
            super(message, cause);
        }

        public ReflectionException(String message) {
            super(message);
        }

        public ReflectionException(Throwable cause) {
            super(cause);
        }
    }

    public static boolean isArray(Object obj) {
        if (obj == null ) return false;
        return obj.getClass().isArray();
    }

    public static boolean isStaticField(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    @SuppressWarnings("unchecked")
    public static <V> V[] array(List<V> list) {
        if (list.size() > 0) {
            Object newInstance = Array.newInstance(list.get(0).getClass(),
                    list.size());
            return (V[]) list.toArray((V[]) newInstance);
        } else {
            die("array(list): The list has to have at least one item in it");
            return null;
        }
    }


    public static int len(Object obj) {
        if (isArray(obj)) {
            return arrayLength(obj);
        } else if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length();
        } else if (obj instanceof Collection) {
            return ((Collection<?>) obj).size();
        } else if (obj instanceof Map) {
            return ((Map<?, ?>) obj).size();
        } else {
            die("Not an array like object");
            return 0; //will never get here.
        }
    }


    public static int arrayLength(Object obj) {
        return Array.getLength(obj);
    }


    private static void handle(Exception ex) {
        throw new ReflectionException(ex);
    }

    private static Class<?> clazz(Object that) {
        if (that instanceof Class) {
            return (Class<?>) that;
        } else {
            return that.getClass();
        }
    }

    public static Object idx(Object object, int index) {
        if (isArray(object)) {
            object = Array.get(object, index);
        } else if (object instanceof List) {
            object = Lists.idx((List) object, index);
        }
        return object;
    }

    public static void idx(Object object, int index, Object value) {
        try {
            Array.set(object, index, value);
        } catch (Exception notExpected) {
            String msg = lines("An unexpected error has occurred",
                    "This is likely a programming error!",
                    String.format("Object is %s, index is %s, and set is %s", object, index, value),
                    String.format("The object is an array? %s", object == null ? "null" : object.getClass().isArray()),
                    String.format("The object is of type %s", object == null ? "null" : object.getClass().getName()),
                    String.format("The set is of type %s", value == null ? "null" : value.getClass().getName()),

                    ""

            );
            Exceptions.handle(msg, notExpected);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProperty(Class<T> t, Object object, final String key) {
        return (T) getProp(object, key);
    }


    public static Object getCollecitonProp(Object o, String propName, int index, String[] path) {
        o = getFieldValues(o, propName);

        if (index + 1 == path.length) {
            return o;
        } else {
            index++;
            return getCollecitonProp(o, path[index], index, path);
        }
    }


    public static Object getPropByPath(Object item, String... path) {
        Object o = item;
        for (int index = 0; index < path.length; index++) {
            String propName = path[index];
            if (o == null) {
                return null;
            } else if (isArray(o) || o instanceof Collection) {
                o = getCollecitonProp(o, propName, index, path);
                break;
            } else {
                o = getProp(o, propName);
            }
        }
        return Conversions.unifyList(o);
    }

    public static <T> List<T> getListOfProps(Class<T> cls, Collection items, String... path) {
        return (List<T>) getPropByPath(items, path);
    }

    public static Object getProp(Object object, final String key) {
        if (object == null) {
            return null;
        }

        Class<?> cls = object.getClass();

        Map<String, FieldAccess> fields = getPropertyFieldAccessors(cls);

        if (!fields.containsKey(key)) {
            fields = getAllAccessorFields(cls);
        }

        if (!fields.containsKey(key)) {
            return null;
        } else {
            return fields.get(key).getValue(object);
        }

    }

    public static void getFields(Object object, final String key, Collection col) {
        if (isArray(object) || object instanceof Collection) {
            Iterator iter = Conversions.iterator(object);
            while (iter.hasNext()) {
                col.add(iter.next());
            }
        } else {
            col.add(getFieldValue(object, key));
        }

    }

    private static Object getFieldValues(Object object, final String key) {
        if (object == null) {
            return null;
        }
        if (isArray(object) || object instanceof Collection) {
            Iterator iter = Conversions.iterator(object);
            List list = new ArrayList(len(object));
            while (iter.hasNext()) {
                list.add(getFieldValues(iter.next(), key));
            }
            return list;
        } else {
            return getFieldValue(object, key);
        }
    }


    private static Object getFieldValue(Object object, final String key) {
        if (object == null) {
            return null;
        }

        Class<?> cls = object.getClass();

        Map<String, FieldAccess> fields = getPropertyFieldAccessMap(cls);

        if (!fields.containsKey(key)) {
            return null;
        } else {
            return fields.get(key).getValue(object);
        }
    }


    private static final boolean useUnsafe = _useUnsafe;

    @SuppressWarnings("unchecked")
    public static <T> T fromMap(Map<String, Object> map, Class<T> clazz) {

        if (map.get("class") == null) {
            map.put("class", clazz.getName());
        }
        return (T) fromMap(map);
    }


    public static Object newInstance(String className) {
        Class<?> clazz = null;

        try {
            clazz = Class.forName(className);
            return newInstance(clazz);
        } catch (Exception ex) {
            log.info(String.format("Unable to create this class %s", className));
            return null;
        }
    }

    public static <T> T newInstance(Class<T> clazz) {
        T newInstance = null;

        try {

            newInstance = clazz.newInstance();
        } catch (Exception ex) {
            handle(ex);
        }

        return newInstance;

    }


    @SuppressWarnings("unchecked")
    public static Object fromMap(Map<String, Object> map) {
        String className = (String) map.get("class");
        Object newInstance = newInstance(className);
        if (newInstance == null) {
            log.info("we were not able to load the class so we are leaving this as a map");
            return map;
        }

        Collection<FieldAccess> fields = getAllAccessorFields(newInstance.getClass()).values();

        for (FieldAccess field : fields) {
            String name = field.getName();
            Object value = map.get(name);
            if (value instanceof Map && Conversions.getKeyType((Map<?, ?>) value) == Typ.string) {
                value = fromMap((Map<String, Object>) value);
            } else if (value instanceof Collection || value instanceof Map[]) {
                listOfMaps(newInstance, field, value);
                continue;
            }

            if (value != null) {
                field.setValue(newInstance, value);
            }
        }

        return newInstance;
    }

    private static void listOfMaps(Object newInstance, FieldAccess field, Object value) {
        if (value instanceof Collection) {
            Class<?> componentType = getComponentType((Collection<?>) value);
            if (Conversions.isMap(componentType)) {
                handleCollectionOfMaps(newInstance, field,
                        (Collection<Map<?, ?>>) value);
            }
        } else if (value instanceof Map[]) {
            Map<?, ?>[] maps = (Map<?, ?>[]) value;
            List<Map<?, ?>> list = Arrays.asList(maps);
            handleCollectionOfMaps(newInstance, field,
                    list);
        }
    }

    @SuppressWarnings("unchecked")
    private static void handleCollectionOfMaps(Object newInstance,
                                               FieldAccess field, Collection<Map<?, ?>> value) {

        Class<?> type = field.getType();
        Collection<Object> target = null;
        try {
            if (!type.isInterface()) {
                Constructor<?> constructor = type.getConstructor(Typ.intgr);
                constructor.setAccessible(true);
                target = (Collection<Object>) constructor.newInstance(value
                        .size());
            } else {
                // the type was an interface so let's see if we can figure out
                // what it should be
                Collection<Object> value2 = (Collection<Object>) field
                        .getValue(newInstance);

                if (value2 != null) {

                    if (Conversions.isModifiableCollection(value2)) {
                        target = value2;
                    }
                }
                if (target == null) {
                    target = (Collection<Object>) createCollection(type, value.size());
                }
            }

            if (value.size() > 0) {
                Map<?, ?> item = value.iterator().next();
                if (Conversions.getKeyType(item) == Typ.string) {
                    for (Map<?, ?> i : value) {
                        target.add(fromMap((Map<String, Object>) i));
                        field.setValue(newInstance, target);
                        return;
                    }
                } else {
                    log.warning(
                            "This should not happen, but for some reason there is a type and we don't know how to convert it");

                }
            } else {
                field.setValue(newInstance, target);
                return;
            }

        } catch (Exception e) {
            log.log(WARNING, "This should not happen, but for some reason we were not able to get the constructor",
                    e);
        }
    }

    public static Collection<Object> createCollection(Class<?> type, int size) {
        if (type == List.class) {
            return new ArrayList<Object>(size);
        } else if (type == SortedSet.class) {
            return new TreeSet<Object>();
        } else if (type == Set.class) {
            return new HashSet<Object>(size);
        } else if (type == Queue.class) {
            return new LinkedList<Object>();
        } else {
            return new ArrayList<Object>(size);
        }
    }

    public static Map<String, Object> toMap(final Object object) {

        if (object == null) {
            return null;
        }

        Map<String, Object> map = new LinkedHashMap<>();


        class FieldToEntryConverter implements
                Conversions.Converter<Maps.Entry<String, Object>, FieldAccess> {
            @Override
            public Maps.Entry<String, Object> convert(FieldAccess from) {
                if (from.isReadOnly()) {
                    return null;
                }
                Maps.Entry<String, Object> entry = new Maps.EntryImpl<>(from.getName(),
                        from.getValue(object));
                return entry;
            }
        }
        List<FieldAccess> fields = new ArrayList(getAllAccessorFields(object.getClass()).values());


        Collections.reverse(fields); // make super classes fields first that
        // their update get overriden by
        // subclass fields with the same name

        List<Maps.Entry<String, Object>> entries = Conversions.mapFilterNulls(
                new FieldToEntryConverter(), new ArrayList(fields));

        map.put("class", object.getClass().getName());

        for (Maps.Entry<String, Object> entry : entries) {
            Object value = entry.value();
            if (value == null) {
                continue;
            }
            if (Conversions.isBasicType(value)) {
                map.put(entry.key(), entry.value());
            } else if (isArray(value)
                    && Conversions.isBasicType(value.getClass().getComponentType())) {
                map.put(entry.key(), entry.value());
            } else if (isArray(value)) {
                int length = arrayLength(value);
                List<Map<String, Object>> list = new ArrayList<>(length);
                for (int index = 0; index < length; index++) {
                    Object item = idx(value, index);
                    list.add(toMap(item));
                }
                map.put(entry.key(), list);
            } else if (value instanceof Collection) {
                Collection<?> collection = (Collection<?>) value;
                Class<?> componentType = getComponentType(collection);
                if (Conversions.isBasicType(componentType)) {
                    map.put(entry.key(), value);
                } else {
                    List<Map<String, Object>> list = new ArrayList<>(
                            collection.size());
                    for (Object item : collection) {
                        if (item != null) {
                            list.add(toMap(item));
                        } else {

                        }
                    }
                    map.put(entry.key(), list);
                }
            } else if (value instanceof Map) {

            } else {
                map.put(entry.key(), toMap(value));
            }
        }
        return map;
    }

    public static Class<?> getComponentType(Collection<?> value) {
        if (value.size() > 0) {
            Object next = value.iterator().next();
            return next.getClass();
        } else {
            return Typ.object;
        }
    }

    private static class FieldConverter implements Conversions.Converter<FieldAccess, Field> {

        boolean thisUseUnsafe;

        FieldConverter(boolean useUnsafe) {
            this.thisUseUnsafe = useUnsafe;
        }

        @Override
        public FieldAccess convert(Field from) {
            if (useUnsafe && thisUseUnsafe) {
                return UnsafeField.createUnsafeField(from);
            } else {
                return new ReflectField(from);
            }
        }
    }

    static Map<String, Map<String, FieldAccess>> allAccessorFieldsCache = new ConcurrentHashMap<>();

    public static Map<String, FieldAccess> getAllAccessorFields(
            Class<? extends Object> theClass) {
        return getAllAccessorFields(theClass, true);
    }

    public static Map<String, FieldAccess> getAllAccessorFields(
            Class<? extends Object> theClass, boolean useUnsafe) {
        Map<String, FieldAccess> map = allAccessorFieldsCache.get(theClass.getName() + useUnsafe);
        if (map == null) {
            List<FieldAccess> list = Conversions.map(new FieldConverter(useUnsafe), getAllFields(theClass));
            map = collectionToMap("name", list);
            allAccessorFieldsCache.put(theClass.getName() + useUnsafe, map);

        }
        return map;
    }


    public static <V> Map<String, V> collectionToMap(String propertyKey, Collection<V> values) {
        LinkedHashMap<String, V> map = new LinkedHashMap<String, V>(values.size());
        Iterator<V> iterator = values.iterator();
        for (V v : values) {
            String key = Reflection.getProperty(Typ.string, v, propertyKey);
            map.put(key, v);
        }
        return map;
    }

    public static List<Field> getAllFields(Class<? extends Object> theClass) {
        List<Field> list = getFields(theClass);
        while (theClass != Typ.object) {

            theClass = theClass.getSuperclass();
            getFields(theClass, list);
        }
        return list;
    }

    public static Map<String, FieldAccess> getPropertyFieldAccessors(
            Class<? extends Object> theClass) {


        Map<String, FieldAccess> fields = allAccessorFieldsCache.get(theClass.getName() + "PROPS");
        if (fields == null) {
            Map<String, Pair<Method>> methods = getPropertySetterGetterMethods(theClass);

            fields = new LinkedHashMap<>();

            for (Map.Entry<String, Pair<Method>> entry :
                    methods.entrySet()) {

                final Pair<Method> methodPair = entry.getValue();
                final String key = entry.getKey();

                PropertyField pf = new PropertyField( key , methodPair.getFirst(), methodPair.getSecond() );

                fields.put( key, pf );

            }

            allAccessorFieldsCache.put(theClass.getName() + "PROPS", fields);
        }


        return fields;
    }

    public static List<Method> getPropertyGetterMethods(
            Class<? extends Object> theClass) {

        Method[] methods = theClass.getMethods();

        List<Method> methodList = new ArrayList<Method>(methods.length);

        for (int index = 0; index < methods.length; index++) {
            Method method = methods[index];
            String name = method.getName();

            boolean staticFlag = Modifier.isStatic(method.getModifiers());


            if (staticFlag || method.getParameterTypes().length > 0
                    || method.getReturnType() == Void.class
                    || !(name.startsWith("get") || name.startsWith("is"))
                    || name.equals("getClass")) {
                continue;
            }
            methodList.add(method);

        }
        return methodList;
    }


    public static List<Method> getPropertySetterMethods(
            Class<? extends Object> theClass) {

        Method[] methods = theClass.getMethods();

        List<Method> methodList = new ArrayList<>(methods.length);


        for (int index = 0; index < methods.length; index++) {
            Method method = methods[index];
            String name = method.getName();
            boolean staticFlag = Modifier.isStatic(method.getModifiers());


            if (!staticFlag && method.getParameterTypes().length == 1
                    && method.getReturnType() == Void.class
                    && name.startsWith("set")) {
                methodList.add(method);
            }

        }
        return methodList;
    }

    public static Map<String, Pair<Method>> getPropertySetterGetterMethods(
            Class<? extends Object> theClass) {

        Method[] methods = theClass.getMethods();

        Map<String, Pair<Method>> methodMap = new LinkedHashMap<>(methods.length);
        List<Method> getterMethodList = new ArrayList<>(methods.length);

        for (int index = 0; index < methods.length; index++) {
            Method method = methods[index];
            String name = method.getName();

            if (method.getParameterTypes().length == 1
                    && method.getReturnType() == void.class
                    && name.startsWith("set")) {
                Pair<Method> pair = new Pair<Method>();
                pair.setFirst(method);
                String propertyName = slc(name, 3);

                propertyName = lower(slc(propertyName, 0, 1)) + slc(propertyName, 1);
                methodMap.put(propertyName, pair);
            }

            if (method.getParameterTypes().length > 0
                    || method.getReturnType() == void.class
                    || !(name.startsWith("get") || name.startsWith("is"))
                    || name.equals("getClass")) {
                continue;
            }
            getterMethodList.add(method);
        }

        for (Method method : getterMethodList) {
            String name = method.getName();
            String propertyName = null;
            if (name.startsWith("is")) {
                propertyName = slc(name, 2);
            } else if (name.startsWith("get")) {
                propertyName = slc(name, 3);
            }

            propertyName = lower(slc(propertyName, 0, 1)) + slc(propertyName, 1);

            Pair<Method> pair = methodMap.get(propertyName);
            if (pair == null) {
                pair = new Pair<Method>();
                methodMap.put(propertyName, pair);
            }
            pair.setSecond(method);

        }
        return methodMap;
    }

    public static void getFields(Class<? extends Object> theClass,
                                 List<Field> list) {
        List<Field> more = getFields(theClass);
        list.addAll(more);
    }

    public static List<Field> getFields(Class<? extends Object> theClass) {
        List<Field> list = Lists.list(theClass.getDeclaredFields());
        for (Field field : list) {
            field.setAccessible(true);
        }
        return list;
    }

    public static <T> T copy(T item) {
        if (item instanceof Cloneable) {
            try {
                Method method = item.getClass().getMethod("clone", (Class[]) null);
                return (T) method.invoke(item, (Object[]) null);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                return fieldByFieldCopy(item);
            }
        } else {
            return fieldByFieldCopy(item);
        }
    }

    public static <K, V> Map<K, V> copy(Map<K, V> collection) {
        Map<K, V> map = new LinkedHashMap();
        for (Map.Entry<K, V> entry : collection.entrySet()) {
            map.put(entry.getKey(), entry.getValue());

        }
        return map;
    }

    private static <T> T fieldByFieldCopy(T item) {
        Map<String, FieldAccess> fields = getAllAccessorFields(item.getClass());
        T clone = null;
        try {
            clone = (T) item.getClass().newInstance();
        } catch (Exception e) {
            handle(e);
        }
        for (FieldAccess field : fields.values()) {
            if (field.isStatic() || field.isFinal() || field.isReadOnly()) {
                continue;
            }
            field.setValue(clone, field.getValue(item));
        }
        return clone;
    }





    public static Iterator iterator(final Object o) {
        if (o instanceof Collection) {
            return ((Collection) o).iterator();
        } else if (isArray(o)) {
            return new Iterator() {
                int index = 0;
                int length = len(o);

                @Override
                public boolean hasNext() {
                    return index < length;
                }

                @Override
                public Object next() {
                    Object value = Reflection.idx(o, index);
                    index++;
                    return value;
                }

                @Override
                public void remove() {
                }
            };
        }
        return null;
    }





    public static String joinBy(char delim, Object... args) {
        StringBuilder builder = new StringBuilder(256);

        if (args.length == 1 && isArray(args[0])) {
            Object array = args[0];
            for (int index = 0; index < len(array); index++) {
                Object obj = Reflection.idx(array, index);
                builder.append(obj.toString());
                if (!(index == args.length - 1)) {
                    builder.append(delim);
                }

            }
        } else {
            int index = 0;
            for (Object arg : args) {
                builder.append(arg.toString());
                if (!(index == args.length - 1)) {
                    builder.append(delim);
                }
                index++;
            }
        }
        return builder.toString();
    }




    @SuppressWarnings({"rawtypes"})
    public static <T> T get(Class<T> clz, Object map, Object key) {
        if (map instanceof Map) {
            return get(clz, (Map) map, key);
        } else {
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T get(Class<T> clz, Map map, Object key) {
        Object value = map.get(key.toString());
        if (value == null) {
            return (T) value;
        }
        if (value.getClass() != clz) {
            T t = Conversions.coerce(clz, value);
            return t;
        } else {
            return (T) value;
        }
    }


}
