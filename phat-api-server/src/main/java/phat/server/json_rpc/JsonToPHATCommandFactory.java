/*
 * Copyright (C) 2016 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the 
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phat.server.json_rpc;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reflections.Reflections;
import phat.commands.PHATCommParam;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandAnn;

/**
 *
 * @author pablo
 */
public final class JsonToPHATCommandFactory {

    Map<String, Class> commandClasses = new HashMap<>();
    Map<String, List<Method>> commandSetMethods = new HashMap<>();
    Map<String, PHATCommandAnn> commandAnnotations = new HashMap<>();

    public JsonToPHATCommandFactory() {
        addAllCommandBuilders();
    }

    private void addAllCommandBuilders() {

        System.out.println("Reflections:::::::::::::::::::::::::::::::::::::::::::::::::");
        Reflections reflections = new Reflections("phat");
        Set<Class<?>> commands = reflections.getTypesAnnotatedWith(PHATCommandAnn.class);
        for (Class<?> commandClass : commands) {
            PHATCommandAnn cAnn = commandClass.getAnnotation(PHATCommandAnn.class);
            System.out.println("-" + cAnn.name());
            commandClasses.put(cAnn.name(), commandClass);
            commandSetMethods.put(cAnn.name(), getSetMethodOrdered(commandClass));
            commandAnnotations.put(cAnn.name(), cAnn);
        }
    }

    public List<String> getMethodNames() {
        return new ArrayList<>(commandClasses.keySet());
    }

    public PHATCommand createCommand(JSONRPC2Request request) {
        System.out.println("createCommand: " + request);
        PHATCommand result = null;
        Class commandClass = commandClasses.get(request.getMethod());
        if (commandClass != null) {
            System.out.println("\tClass: " + commandClass.getSimpleName());
            try {
                List<Object> positionalParams = request.getPositionalParams();
                if (positionalParams != null) {
                    Object commandObj = commandClass.newInstance();
                    List<Method> methods = commandSetMethods.get(request.getMethod());

                    for (int i = 0; i < positionalParams.size(); i++) {
                        Method m = methods.get(i);
                        try {
                            System.out.println("valueType = " + m.getParameterTypes()[0].getCanonicalName());
                            Object value = getRightInstance(m, (String) positionalParams.get(i));
                            m.invoke(commandObj, value);
                        } catch (IllegalArgumentException | InvocationTargetException ex) {
                            Logger.getLogger(JsonToPHATCommandFactory.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    return (PHATCommand) commandObj;
                } else {
                    Map<String, Object> namedParams = request.getNamedParams();
                    if (namedParams != null) {

                    } else {
                        // command without params!
                        return (PHATCommand) commandClass.newInstance();
                    }
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(JsonToPHATCommandFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    private Object getRightInstance(Method m, String value) {
        Class<?> type = m.getParameterTypes()[0];
        if (type.isPrimitive()) {
            String typeName = type.getSimpleName();
            if (typeName.equalsIgnoreCase("boolean")) {
                return Boolean.valueOf(value);
            } else if (typeName.equalsIgnoreCase("float")) {
                return Float.valueOf(value);
            } else if (typeName.equalsIgnoreCase("double")) {
                return Double.valueOf(value);
            } else if (typeName.equalsIgnoreCase("int")) {
                return Integer.valueOf(value);
            }
        } else {
            try {
                Method valueOf = type.getMethod("valueOf", String.class);
                return valueOf.invoke(null, value);
            } catch (SecurityException | IllegalAccessException | 
                    IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
                Logger.getLogger(JsonToPHATCommandFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return value;
    }

    private Method getMethod(String method, String paramName) {
        String methodName = "set" + paramName.substring(0, 1).toUpperCase() + paramName.substring(1);
        for (Method m : commandSetMethods.get(method)) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        return null;
    }

    private boolean isMandatory(Method method) {
        PHATCommParam cp = method.getAnnotation(PHATCommParam.class);
        if (cp != null) {
            return cp.mandatory();
        }
        return false;
    }

    private int getOrder(Method method) {
        return method.getAnnotation(PHATCommParam.class).order();
    }

    private List<Method> getSetMethodOrdered(Class commandClass) {
        List<Method> methods = new ArrayList<>();
        for (Method m : commandClass.getMethods()) {
            if (m.getAnnotation(PHATCommParam.class) != null && m.getName().startsWith("set")) {
                if (methods.isEmpty()) {
                    methods.add(m);
                } else {
                    int size = methods.size();
                    for (int i = 0; i < size; i++) {
                        Method m1 = methods.get(i);
                        if (getOrder(m) < getOrder(m1)) {
                            methods.add(i, m);
                        }
                    }
                    if (size == methods.size()) {
                        methods.add(m);
                    }
                }
            }
        }
        return methods;
    }

    private String setNameToParamName(Method method) {
        System.out.println("setNameToParamName...");
        Class<?>[] types = method.getParameterTypes();
        Class<?> paramClass = types[0];
        String typeName = paramClass.getSimpleName();
        System.out.println("typeName = " + typeName);
        if (typeName.equalsIgnoreCase("boolean")) {
            return "true|false";
        } else if (paramClass.isEnum()) {
            System.out.println("isEnum");
            int enumNum = paramClass.getEnumConstants().length;
            Field[] flds = paramClass.getDeclaredFields();
            String result = "";
            for (int i = 0; i < flds.length; i++) {
                Field f = flds[i];
                if (f.isEnumConstant()) {
                    result += f.getName();
                    if (i < enumNum - 1) {
                        result += "|";
                    }
                }
            }
            return result;
        }
        String setMethodName = method.getName();
        return setMethodName.substring(3, 4).toLowerCase() + setMethodName.substring(4);
    }

    public boolean isHandable(JSONRPC2Request request) {
        String method = request.getMethod();
        return commandClasses.get(method) != null;
    }

    public String getAnnType(String method) {
        PHATCommandAnn ann = commandAnnotations.get(method);
        if (ann != null) {
            return ann.type();
        }
        return null;
    }

    public boolean isAnnDebug(String method) {
        PHATCommandAnn ann = commandAnnotations.get(method);
        if (ann != null) {
            return ann.debug();
        }
        return false;
    }

    public String getUsage(String method) {
        String result = method;
        System.out.println("Method usage = " + method);
        for (Method m : commandSetMethods.get(method)) {
            String parName = setNameToParamName(m);
            if (isMandatory(m)) {
                result += " <" + parName + ">";
            } else {
                result += " [" + parName + "]";
            }
        }
        return result;
    }
}
