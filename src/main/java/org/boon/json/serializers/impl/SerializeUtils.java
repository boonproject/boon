package org.boon.json.serializers.impl;

import org.boon.Boon;
import org.boon.core.Type;
import org.boon.json.serializers.CustomObjectSerializer;
import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.primitive.CharBuf;

import java.util.Map;
import java.util.Set;

/**
 * Created by Richard on 9/16/14.
 */
public class SerializeUtils {


    public static void  handleInstance(JsonSerializerInternal jsonSerializer,
                                        Object obj, CharBuf builder,
                                        Map<Class, CustomObjectSerializer> overrideMap,
                                        Set<Class> noHandle, boolean typeInfo,
                                        Type type) {
        if (overrideMap!=null) {
            Class<?> cls = Boon.cls(obj);
            if (cls != null && !cls.isPrimitive() && !noHandle.contains(cls)) {
                CustomObjectSerializer customObjectSerializer = overrideMap.get(cls);
                if (customObjectSerializer != null) {
                    customObjectSerializer.serializeObject(jsonSerializer, obj, builder);
                    return;
                }
                customObjectSerializer = overrideMap.get(cls.getSuperclass());
                if (customObjectSerializer != null) {
                    overrideMap.put(cls.getSuperclass(), customObjectSerializer); //Remember this
                    customObjectSerializer.serializeObject(jsonSerializer, obj, builder);
                    return;
                }

                final Class<?>[] interfaces = cls.getInterfaces();
                for (Class interf : interfaces) {

                    customObjectSerializer = overrideMap.get(interf);
                    if (customObjectSerializer != null) {
                        overrideMap.put(interf, customObjectSerializer); //Remember this
                        customObjectSerializer.serializeObject(jsonSerializer, obj, builder);
                        return;
                    }

                }

                noHandle.add(cls);

            }
        }

        if (type == Type.INSTANCE) {
            jsonSerializer.serializeInstance(obj, builder, typeInfo);
        } else {
            jsonSerializer.serializeSubtypeInstance(obj, builder);
        }
        return;
    }

}
