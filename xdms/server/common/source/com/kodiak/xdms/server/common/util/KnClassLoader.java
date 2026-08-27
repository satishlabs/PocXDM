/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnClassLoader.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         26-03-2007 6.0
 *
 *
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 **************************************************************************/
package com.kodiak.xdms.server.common.util;

import com.kodiak.logger.KnLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This is a utility class which provides APIs for loading class, creating instance
 * of a class, geting methods, invoking methods in an object etc. This class mainly
 * does reflection related stuf.
 */
public class KnClassLoader {
	private static final KnLogger knLogger = KnLogger.getLogger(KnClassLoader.class);

    private static final String CLASS = KnClassLoader.class.getName();
    /**
     * Load class of class name passed
     *
     * @param className
     * @return Class
     * @throws com.kodiak.xdms.server.common.util.KnClassLoaderException
     */
    public static Class loadClass(String className) throws KnClassLoaderException {
        String methodName = "loadClass";
        Class classObj = null;
        if (className == null || className.trim().equals("")) {
            knLogger.error( methodName, "Class Name is empty");
            throw new KnClassLoaderException("Class name is empty/null", className);
        }

        knLogger.debug( methodName, "Loading class " + className);
        try {
            classObj = Class.forName(className);
        } catch (ClassNotFoundException e) {
            knLogger.error( methodName, "Class Not found - " + e.getMessage());
            throw new KnClassLoaderException("Class Not Found", e, className);
        }
        knLogger.debug( methodName, "Class Loaded");
        return classObj;
    }

    /**
     * Create the instance of the class using defult constructor.
     *
     * @param className
     * @return the class instance
     * @throws KnClassLoaderException
     */
    public static Object createInstance(String className) throws KnClassLoaderException {
        String methodName = "createInstance";
        knLogger.debug( methodName, "Create instance of " + className);
        return createInstance(loadClass(className));
    }

    /**
     * Create an instanceof the class using the default constructor
     * @param classObj
     * @return instance
     * @throws KnClassLoaderException
     */
    public static Object createInstance(Class classObj) throws KnClassLoaderException {
        String methodName = "createInstance";
        try {
            knLogger.debug( methodName, "Creating instance...");
            Object obj = classObj.getDeclaredConstructor().newInstance();
            knLogger.debug( methodName, "Instance created - " + obj);
            return obj;
        } catch (InstantiationException | InvocationTargetException e) {
            knLogger.error( methodName, "Instantiation Error - " + e.getMessage());
            throw new KnClassLoaderException("Cannot Instantiate class", e, classObj.toString());
        } catch (IllegalAccessException | NoSuchMethodException e) {
            knLogger.error( methodName, "Illegal Access Error - " + e.getMessage());
            throw new KnClassLoaderException("Illegal Access error", e, classObj.toString());
        }
    }

    /**
     * Return the method of the class. The method should not take any parameters.
     * @param className
     * @param declaredMethodName
     * @return Method
     * @throws KnClassLoaderException
     */
    public static Method getDeclaredMethod(String className, String declaredMethodName)
        throws KnClassLoaderException {
        String methodName = "getDeclaredMethod";
        knLogger.debug( methodName, "Geting method : Class Name -> " + className + ", Method : " + declaredMethodName);
        Class classObj = loadClass(className);
        try {
            Method method = classObj.getMethod(declaredMethodName, new Class[]{});
            knLogger.debug( methodName, "Retrieved declared method " + method);
            return method;
        } catch (NoSuchMethodException e) {
            knLogger.error( methodName, "Method Not found - " + e.getMessage());
            throw new KnClassLoaderException("Method Not found - " + methodName, e, className);
        }
    }

    /**
     * Invoke the method in the given object and return the value.
     * @param method
     * @param obj
     * @return
     * @throws KnClassLoaderException
     */
    public static Object invoke(Method method, Object obj)
        throws KnClassLoaderException {
        String methodName = "invoke";
        try {
            knLogger.debug( methodName, "Invoking " + method + " in " + obj);
            Object out = method.invoke(obj, new Object[]{});
            knLogger.debug( methodName, "Invoked. Returned Value -> " + out);
            return out;
        } catch (IllegalAccessException e) {
            knLogger.error( methodName, "Illegal Access Error - " + e.getMessage());
            throw new KnClassLoaderException("Illegal Access Error - " + e.getMessage(), e, obj.getClass().getName());
        } catch (InvocationTargetException e) {
            knLogger.error( methodName, "Invocation Error - " + e.getMessage());
            throw new KnClassLoaderException("Invocation Error - " + e.getMessage(), e, obj.getClass().getName());
        }
    }
}
