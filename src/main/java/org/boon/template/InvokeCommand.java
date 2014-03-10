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

package org.boon.template;


import org.boon.Lists;
import org.boon.core.Typ;
import org.boon.core.reflection.Invoker;
import org.boon.core.reflection.MethodAccess;
import org.boon.primitive.CharBuf;

import java.util.List;

import static org.boon.json.JsonFactory.fromJson;

public class InvokeCommand implements Command{


    private Object function;


    private String methodName;
    private MethodAccess methodAccess;

    private String commandName;



    public InvokeCommand(Object function, MethodAccess methodAccess) {
        this.methodAccess = methodAccess;
        this.function = function;
    }

    public InvokeCommand(Object function) {
        this.function = function;
        init();
    }

    public InvokeCommand(Object function, String methodName) {
        this.function = function;
        this.methodName = methodName;
        init();
    }

    public void init() {

        if (function instanceof  Class && methodName !=null) {
            methodAccess = Invoker.invokeMethodAccess((Class) function, methodName);
        } else if (methodName!=null) {
            methodAccess = Invoker.invokeMethodAccess(function, methodName);
        } else {
            methodAccess = Invoker.invokeFunctionMethodAccess(function);
        }
    }

    @Override
    public void processCommand(CharBuf output, String args, CharSequence block, Object context) {

        block = block.toString();

        if (args.trim().startsWith("[") || args.startsWith("{") || args.startsWith("'")) {

            String arguments = BoonTemplate.template("${", "}").replace(args, context).toString();

            Object o = fromJson(arguments.replace('\'', '"'));

            if (o instanceof List) {
                List<Object> argList = Lists.copy((List)o);

                if (commandName!=null) {
                    argList.add(0, commandName);
                }

                addArgs(block, context, argList);
                addArgs(block, context, argList);

                /** If it returns a java.lang.String or chars[] then don't pass the output buffer as the first arg. */
                if (methodAccess.returnType() == Typ.string || methodAccess.returnType() == Typ.chars) {
                    CharSequence out = (CharSequence) Invoker.invokeFromList(function, methodAccess.name(), argList);
                    output.add(out);
                } else {
                    Invoker.invokeFromList(function, methodAccess.name(), argList);
                }
            }
        } else {


            /** If it returns a java.lang.String or chars[] then don't pass the output buffer as the first arg. */
            if (methodAccess.returnType() == Typ.string || methodAccess.returnType() == Typ.chars) {

                CharSequence out;
                if (commandName!=null) {
                    out = (CharSequence)methodAccess.invoke(commandName, function, args, block.toString(), context);
                } else {
                    out = (CharSequence)methodAccess.invoke(function, args, block.toString(), context);
                }
                output.add(out);
            } else {
                if (commandName!=null) {
                    methodAccess.invoke(commandName, function, output, args, block.toString(), context);
                } else {
                    methodAccess.invoke(function, output, args, block.toString(), context);
                }

            }
        }

    }

    private void addArgs(CharSequence block, Object context, List<Object> argList) {
        if (argList.size() < methodAccess.parameterTypes().length) {
            if (Typ.isCharSequence(methodAccess.parameterTypes()[argList.size()]) ) {
                argList.add(block);
            } else {
                argList.add(context);
            }
        }
    }
}
