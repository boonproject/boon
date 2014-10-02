/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.expression;

import org.boon.*;
import org.boon.core.Conversions;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.ClassMeta;
import org.boon.core.reflection.MethodAccess;
import org.boon.json.JsonParserAndMapper;
import org.boon.json.JsonParserFactory;
import org.boon.primitive.Arry;

import java.util.*;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Str.slc;
import static org.boon.json.JsonFactory.fromJson;

/**
 * Created by Richard on 2/10/14.
 */
public class BoonExpressionContext implements ExpressionContext {


    private LinkedList<Object> context;

    private BoonExpressionContext parent;

    private final JsonParserAndMapper jsonParser = new JsonParserFactory().lax().create();


    /** Functions can be used anywhere where expressions can be used. */
    protected Map<String, MethodAccess> staticMethodMap = new HashMap<>(100);


    public BoonExpressionContext(final List<Object> root) {


        this.context = new LinkedList<>();

        if (root!=null) {
            this.context.add(root);
        }

        addFunctions("fn", StandardFunctions.class);

        parent = this;

    }

    public void addFunctions(String prefix, Class<?> functions) {

        final ClassMeta<?> funcs = ClassMeta.classMeta(functions);


        for (MethodAccess m : funcs.methods()) {
            if (m.isStatic()) {
                String funcName = Str.add(prefix, ":", m.name());
                staticMethodMap.put(funcName, m);
            }
        }
    }

    public void initContext(Object... array) {

        this.context = new LinkedList<>();
        if (array==null) {
            return;
        }

        for (Object root : array ) {
            if (root == null) {
                continue;
            }
            if (root instanceof CharSequence) {

                String str = root.toString().trim();

                if (str.startsWith("[") || str.startsWith("{")) {
                    this.context.add(
                            fromJson(root.toString()
                            )
                    );
                }

            } else {
                this.context.add(root);
            }

            parent=this;
        }

    }


    public void addFunctions(Class<?> functionsClass) {

        final ClassMeta<?> standardFunctionsClassMeta = ClassMeta.classMeta(functionsClass);


        for (MethodAccess m : standardFunctionsClassMeta.methods()) {
            if (m.isStatic()) {
                String funcName = Str.add(m.name());
                staticMethodMap.put(funcName, m);
            }
        }
    }

    public BoonExpressionContext(final Object... array) {



        this.context = new LinkedList<>();


        initContext(array);

        addFunctions("fn", StandardFunctions.class);

    }







    @Override
    public char idxChar( String property ) {

        return Conversions.toChar(this.lookup(property));
    }



    Object findProperty(String propertyPath, boolean searchChildren) {


        //We have to cache the prop path and create a lookup object that we look up
        //too expensive but ok for now.

        Object defaultValue;

        String formatRule = null;


        Object outputValue=null;


        if (propertyPath.indexOf('|') != -1) {

            String[] splitByPipe = Str.splitByPipe(propertyPath);
            defaultValue = splitByPipe[1];
            propertyPath = splitByPipe[0];

        } else {
            defaultValue = null;
        }

        if (propertyPath.indexOf('%') != -1) {

            String[] splitByPercentSign = StringScanner.split(propertyPath, '%', 1);
            formatRule = splitByPercentSign[1];
            propertyPath = splitByPercentSign[0];
        }


        for (Object ctx : this.context) {

            if (searchChildren && ctx instanceof BoonExpressionContext) {
                BoonExpressionContext basicContext = (BoonExpressionContext) ctx;
                outputValue =  basicContext.findProperty(propertyPath, true);
                if (outputValue!=null) {
                    break;
                }
            } else if (ctx instanceof Pair) {
                Pair<String, Object> pair = (Pair<String, Object>)ctx;
                if(pair.getKey().equals(propertyPath)) {
                    outputValue = pair.getValue();
                    break;
                } else if (propertyPath.startsWith(pair.getKey())){

                    String subPath = StringScanner.substringAfter(
                            propertyPath, pair.getKey());

                    Object o = pair.getValue();
                    outputValue =  BeanUtils.idx(o, subPath);
                    break;
                }

            } else {
                outputValue  = BeanUtils.idx(ctx, propertyPath);
                if (outputValue != null) {

                    break;
                }
            }
        }

        if (outputValue==null) {
            outputValue = defaultValue;
        }

        if (formatRule!=null) {
            outputValue = applyFormatRule(formatRule, outputValue);

        }
        return outputValue;

    }


    private Object applyFormatRule(String formatRule, Object outputValue) {

        return String.format(Str.add("%",formatRule), outputValue);
    }


    @Override
    public byte idxByte( String property ) {


        return Conversions.toByte(this.lookup(property));
    }

    @Override
    public short idxShort( String property ) {

        return Conversions.toShort(this.lookup(property));
    }

    @Override
    public String idxString( String property ) {

        return Conversions.toString(this.lookup(property));
    }

    @Override
    public int idxInt( String property ) {

        return Conversions.toInt(this.lookup(property));
    }

    @Override
    public float idxFloat( String property ) {

        return Conversions.toFloat(this.lookup(property));
    }

    @Override
    public double idxDouble( String property ) {

        return Conversions.toDouble(this.lookup(property));
    }

    @Override
    public long idxLong( String property ) {

        return Conversions.toLong(this.lookup(property));
    }

    @Override
    public Object idx( String property ) {

        return this.lookup(property);
    }

    @Override
    public <T> T idx( Class<T> type, String property ) {

        return (T) this.lookup(property);

    }

    public int size() {
       return context.size();
    }

    public boolean isEmpty() {
        return context.isEmpty();
    }

    public Object get( Object key ) {

        return this.lookup((key.toString()));

    }



    @Override
    public void put(String key, Object value) {
        Pair<String, Object> pair = new Pair<>(key, value);
        context.add(0, pair);
    }


    /**
     * Lookup an object and use its name as the default value if not found.
     *
     * @param objectName
     * @return
     */
    public Object lookup(String objectName) {
        return lookupWithDefault(objectName, objectName);
    }




    /**
     * Lookup an object and supply a default value.
     * @param objectExpression
     * @param defaultValue
     * @return
     */

    private Object doLookup(String objectExpression, Object defaultValue, boolean searchChildren) {

        if (Str.isEmpty(objectExpression)) {
            return defaultValue;
        }

        char firstChar = Str.idx(objectExpression, 0);
        char secondChar = Str.idx(objectExpression, 1);
        char lastChar = Str.idx(objectExpression, -1);

        boolean escape = false;

        switch(firstChar) {
            case '$':
                if (lastChar=='}') {
                    objectExpression = slc(objectExpression, 2, -1);
                } else {
                    objectExpression = slc(objectExpression, 1);
                }
                break;
            case '{':
                if (secondChar=='{' && lastChar=='}') {
                    char thirdChar = Str.idx(objectExpression, 2);

                    if (thirdChar == '{') {
                        escape = true;
                        objectExpression = slc(objectExpression, 3, -3);
                    }else {
                        objectExpression = slc(objectExpression, 2, -2);
                    }
                }
                else {
                    if (lastChar == '}') {
                        return jsonParser.parse(objectExpression);
                    } else {
                        escape = true;
                        objectExpression = slc(objectExpression, 1);
                    }
                }
                break;
            case '[':
                return jsonParser.parse(objectExpression);
            case '.':
                if (secondChar=='.') {

                    String newExp = slc(objectExpression, 2);
                    return parent.doLookup(newExp, newExp, false);
                }
        }

        Object value;
        lastChar = Str.idx(objectExpression, -1);
        if (lastChar==')') {
            value = handleFunction(objectExpression, searchChildren);
        } else {


            value = findProperty(objectExpression, searchChildren);

            value = value == null ? defaultValue : value;

        }

        if (!escape) {
            return value;
        } else {
            return StandardFunctions.escapeXml(value);
        }

    }
    public Object lookupWithDefault(String objectExpression, Object defaultValue) {
            return doLookup(objectExpression, defaultValue, true);
    }

    private Object handleFunction(String functionCall, boolean searchChildren) {

        //"$fn:lower($fn:upper(session.request.name))"



        final String[] split = StringScanner.split(functionCall, '(', 1);


        String methodName = split[0];
        String arguments = slc(split[1], 0, -1) ;
        List<Object> args = getObjectFromArguments(arguments, searchChildren);

        MethodAccess method = this.staticMethodMap.get(methodName);

        if (method!=null) {

            return method.invokeDynamic(null, Arry.objectArray(args));
        } else {
            return handleMethodCall(methodName, args);
        }

    }

    private Object handleMethodCall(String objectPath, List<Object> args) {

        final int lastIndexOf = objectPath.lastIndexOf('.');

        String beanPath = objectPath.substring(0, lastIndexOf);
        String methodName = objectPath.substring(lastIndexOf+1, objectPath.length());

        Object bean = lookup(beanPath);

        if (bean == null) {
            return null;
        }

        final Class<?> cls = Boon.cls(bean);

        if (cls == null) {
            return null;
        }

        final ClassMeta<?> classMeta = ClassMeta.classMeta(cls);

        final MethodAccess method = classMeta.method(methodName);

        if (method==null) {
            return null;
        }

        return method.invokeDynamic(bean, Arry.array(args));
    }

    protected List<Object> getObjectFromArguments(String arguments, boolean searchChildren) {

            final String[] strings = StringScanner.splitByChars(arguments, ',');

            List list = new ArrayList();

            for (String string : strings) {
                Object object = doLookup(string, string, searchChildren);
                list.add(object);
            }

            return list;


    }



    public void pushContext(Object value) {
        final BoonExpressionContext child = new BoonExpressionContext((Object) value);
        child.parent = this;
        this.context.add(0, child);
    }


    public void removeLastContext() {
        this.context.remove(0);
    }


}
