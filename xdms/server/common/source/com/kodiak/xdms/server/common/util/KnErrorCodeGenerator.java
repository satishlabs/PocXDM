/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
* Rama Krishna         26-03-2007         6.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * *************************************************************************
 */

package com.kodiak.xdms.server.common.util;

import com.kodiak.xdms.server.common.configuration.KnLoaderException;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheAttributes;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheElement;
import com.kodiak.xdms.server.common.configuration.loader.xml.KnXmlLoader;
import com.kodiak.xdms.server.common.configuration.loader.xml.KnXmlLoaderConfigData;

import java.util.*;

public class KnErrorCodeGenerator {

    private KnCacheElement cacheElement = null;
    private static final String MODULE_ERRORS = "error-codes";
    private static final String ERROR_LABEL = "label";
    private static final String ERROR_CODE = "code";
    private static String ERROR_CODE_CLASS = "KnErrorCodes";
    private static final String ORIGINATORS = "originators";
//    private static final String OUTPUT_DIR = "source";

    private String outDir = null;

    private KnErrorCodeGenerator(String outDir) {
        this.outDir = outDir == null ? "." : outDir;
    }

    public static void main(String args[]) {
        try {
            KnErrorCodeGenerator errorCodeGenerator = new KnErrorCodeGenerator(args[0]);
            errorCodeGenerator.init(args[1]);
            String commonErrorCodes = null;
            String libraryName = "";
            // fifth command-line argument is superclass name.
            // If it have dependency with the super class obj then it needs to extends by all the inner classes of classobj.
            if (args.length == 5) {
                commonErrorCodes = args[3]; // super-class name with fully package name
                libraryName = args[4] + ".";
            } else if (args.length == 6) {
                commonErrorCodes = args[3]; // super-class name with fully package name
                libraryName = args[4] + ".";
                ERROR_CODE_CLASS = args[5];
            }

            errorCodeGenerator.generateErrorCodeJavaFile(args[2], ERROR_CODE_CLASS, commonErrorCodes, libraryName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(String xmlFileName) throws KnLoaderException {
        //Loads the error - codes XML file
        KnXmlLoader loader = new KnXmlLoader();
        loader.setCacheData(new KnCacheElement());
        KnXmlLoaderConfigData loaderConfigData = (KnXmlLoaderConfigData) loader.getConfigData();
        loaderConfigData.setXmlFile(xmlFileName);
        loaderConfigData.setIdLabel(ERROR_CODE);
        cacheElement = (((KnCacheElement) loader.load()).getElement(MODULE_ERRORS));
        if (cacheElement == null)
            System.out.println("Cache Element is null for : " + MODULE_ERRORS);
    }

    public void generateErrorCodeJavaFile(String pkgName, String errorCodeClassName, String superClassName, String libraryName) throws Exception {
        KnCacheElement nextElement = null;
        KnCacheAttributes attributes = null;
        Map classConfigMap = new LinkedHashMap();

        Collection innerClasslist = getInnerClasses(superClassName);
//        System.out.println("INNER CLASSES LIST: " + innerClasslist);
        if (cacheElement != null) {
            for (Iterator iterator = cacheElement.getElements().iterator(); iterator.hasNext();)
            {
                nextElement = (KnCacheElement) iterator.next();
                String fieldValue = nextElement.getId();
                attributes = nextElement.getAttributes();
                String fieldName = attributes.get(ERROR_LABEL);
                String originators = attributes.get(ORIGINATORS);
                String[] orginator = originators.split(",");
                for (int i = 0, len = orginator.length; i < len; i++) {
                    String className = orginator[i].trim();
                    // checks whether orginator hasbeen defined in superclass. If so,
                    // then extends with the superclass
                    if (innerClasslist.contains(className)) {
                        className = (new StringBuffer(className).append(" extends ").append(superClassName).append(".").append(className)).toString();
                        innerClasslist.remove(className);
                    }
                    KnClassConfig classConfig = (KnClassConfig) classConfigMap.get(className);
                    if (classConfig == null) {
                        classConfig = new KnClassConfig("public static ", className);
                        classConfigMap.put(className, classConfig);
                    }
                    System.out.println(className + " " + fieldName + " " + fieldValue);
                    classConfig.addField("public static ", "String", fieldName, libraryName + orginator[i].trim() + "." + fieldValue);
                }
            }
        } else {
            System.out.println("Error code generator not initialized");
        }

        KnClassConfig errorClassConfig = new KnClassConfig("public ", errorCodeClassName);
        errorClassConfig.addField("public static final", "String", "MODULE_NAME", "ERRORCODES");

        //
        for (Iterator iterator = innerClasslist.iterator(); iterator.hasNext(); ) {
            String innerClassName = (String) iterator.next();
            String className = new StringBuffer(innerClassName).append(" extends ").append(superClassName).append(".").append(innerClassName).toString();
            KnClassConfig classConfig = (KnClassConfig) classConfigMap.get(className);
            if (classConfig == null) {
                classConfig = new KnClassConfig("public static ", className);
                classConfigMap.put(className, classConfig);
            }
        }

        for (Iterator it = classConfigMap.values().iterator(); it.hasNext(); ) {
            errorClassConfig.addInnerClass((KnClassConfig)it.next());
        }
        KnJavaFileGenerator fileGenerator = new KnJavaFileGenerator(outDir, pkgName, errorCodeClassName + ".java");
        fileGenerator.addClassConfig(errorClassConfig);
        fileGenerator.generate();
    }

    /**
     * this method will returns the collection of inner classnames which exists
     * in the given className
     * @param className
     * @return Collection of innerClass names
     */
    private Collection getInnerClasses(String className) {
        Collection innerClasslist = new ArrayList();
        if (className != null) {
            try {
                Class[] innerClasses = KnClassLoader.createInstance(className).getClass().getClasses();
//                System.out.println(" LENGTH : " + innerClasses.length );
                for (int i = 0;i < innerClasses.length; i++) {
                    String[] name = innerClasses[i].getName().split("[$]");
                    innerClasslist.add(name[1].trim());
                }
            } catch (KnClassLoaderException e) {
                System.out.println("Failed to load class : " + className);
                // throws exception....
            }
        }
        return innerClasslist;
    }
}
