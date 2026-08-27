/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnClassConfig.java
 * Subsystem:   Server Common
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Rama Krishna         26-03-2007      6.0
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class KnClassConfig {

    private String modifier;

    /** the class name */
    private String className;

    private Map fieldMap = new LinkedHashMap();
    private Map innerClassMap = new LinkedHashMap();

    public KnClassConfig(String modifier, String className) {
        this.modifier = modifier;
        this.className = className;
    }

    /**
     * Returns the class name
     *
     * @return the class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the class name
     *
     * @param className the class name
     */
    public void setClassName(String className) {
        this.className = className;
    }

    public void addField(String modifier, String fieldType, String varName, String value)
        throws Exception {
        if (fieldMap.get(varName) != null) {
            throw new Exception("Variable name " + varName + " already defined.");
        } else {
            String fieldStr = modifier + " " + fieldType + " " + varName;
            if (fieldType.equals("String")) {
                fieldStr +=  " = \"" + value + "\";";
            } else {
                fieldStr +=  " = " + value + ";";
            }
            fieldMap.put(varName, fieldStr);
        }
    }

    public void addInnerClass(KnClassConfig classConfig) throws Exception {
        String className = classConfig.getClassName();
        if (innerClassMap.get(className) == null) {
            innerClassMap.put(className, classConfig);
        } else {
            throw new Exception("Inner class " + className + " already defined.");
        }
    }

    public String getCodeAsString(String classIndent, String memberIndent) {
        StringBuffer buffer = new StringBuffer(500);

        //put class comments
        buffer.append("\n");
        buffer.append(classIndent).append("/**\n");
        buffer.append(classIndent).append(" * ").append(className).append(" class.\n");
        buffer.append(classIndent).append(" */\n");
        buffer.append(classIndent).append((modifier == null? "" : modifier + " ")).append("class ").append(className).append(" {\n");

        // add fields
        for (Iterator it = fieldMap.values().iterator(); it.hasNext();) {
            buffer.append(classIndent).append(memberIndent).append(it.next()).append("\n");
        }

        // add methods if any

        // add Inner classes if any
        for (Iterator it = innerClassMap.values().iterator(); it.hasNext();) {
            KnClassConfig innerClassConfig = (KnClassConfig)it.next();
            buffer.append(classIndent)
                    .append(innerClassConfig.getCodeAsString(classIndent + memberIndent, memberIndent))
                    .append("\n");
        }

        buffer.append(classIndent).append("}");

        return buffer.toString();
    }

    public static void main(String[] args) throws Exception {
        KnClassConfig classConfig = new KnClassConfig("public", "Test");
        classConfig.addField("public", "String", "name", "Jiji");
        classConfig.addField("public", "int", "age", "25");
        classConfig.addField("public", "String", "address", "sfh kdhkh");

        KnClassConfig classConfig1 = new KnClassConfig("static", "TestInner1");
        classConfig1.addField("public", "String", "name", "Jiji");
        classConfig1.addField("public", "int", "age", "25");
        classConfig1.addField("public", "String", "address", "sfh kdhkh");

        KnClassConfig classConfig11 = new KnClassConfig("static", "TestInner1Inner");
        classConfig11.addField("public", "String", "name", "Jiji");
        classConfig11.addField("public", "int", "age", "25");
        classConfig11.addField("public", "String", "address", "sfh kdhkh");

        KnClassConfig classConfig2 = new KnClassConfig("static", "TestInner2");
        classConfig2.addField("public", "String", "name", "Jiji");
        classConfig2.addField("public", "int", "age", "25");
        classConfig2.addField("public", "String", "address", "sfh kdhkh");

        classConfig1.addInnerClass(classConfig11);
        classConfig.addInnerClass(classConfig1);
        classConfig.addInnerClass(classConfig2);

        System.out.println(classConfig.getCodeAsString("", "    "));
    }
}
