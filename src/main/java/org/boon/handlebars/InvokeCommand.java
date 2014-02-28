package org.boon.handlebars;


import org.boon.Lists;
import org.boon.core.Typ;
import org.boon.core.reflection.ClassMeta;
import org.boon.core.reflection.Invoker;
import org.boon.core.reflection.MethodAccess;
import org.boon.primitive.CharBuf;

import java.util.List;

import static org.boon.json.JsonFactory.fromJson;

public class InvokeCommand implements Command{


    private Object function;


    private String methodName;
    private MethodAccess methodAccess;



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

        if (args.trim().startsWith("[") || args.startsWith("{") || args.startsWith("'")) {

            String arguments = HandleBarTemplateParser.template("${", "}").replace(args, context).toString();

            Object o = fromJson(arguments.replace('\'', '"'));

            if (o instanceof List) {
                List<Object> argList = Lists.copy((List)o);

                addArgs(block, context, argList);
                addArgs(block, context, argList);

                if (methodAccess.returnType() == Typ.string || methodAccess.returnType() == Typ.chars) {
                    CharSequence out = (CharSequence) Invoker.invokeFromList(function, methodAccess.name(), argList);
                    output.add(out);
                } else {
                    Invoker.invokeFromList(function, methodAccess.name(), argList);
                }
            }
        } else {
            if (methodAccess.returnType() == Typ.string || methodAccess.returnType() == Typ.chars) {
                CharSequence out = (CharSequence) methodAccess.invoke(function, args, block, context);
                output.add(out);
            } else {
                methodAccess.invoke(function, output, args, block, context);
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
