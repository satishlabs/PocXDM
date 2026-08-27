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
 * Rama Krishna         27-03-2007 6.0
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class KnJavaFileGenerator {

    private static final String CLASSINDENT = "";
    private static final String MEMINDENT = "    ";

    private String packageName;
    private String outputDir;
    private String fileName;

    Map classConfigMap = new LinkedHashMap();

    /**
     * @param outputDir
     * @param packageName
     */
    public KnJavaFileGenerator(String outputDir, String packageName, String fileName) {
        this.packageName = packageName;
        this.outputDir = outputDir;
        this.fileName = fileName;
    }


    public void addClassConfig(KnClassConfig classConfig) throws Exception {
        String className = classConfig.getClassName();
        if (classConfigMap.get(className) == null) {
            classConfigMap.put(className, classConfig);
        } else {
            throw new Exception("Class " + className + " already exists.");
        }
    }


    /**
     */
    public void generate() throws IOException {
        StringBuffer buffer = new StringBuffer(500);
        buffer.append("/***************************************************************************\n");
        buffer.append(" *\n");
        buffer.append(" * File name: ").append(fileName).append("\n");
        buffer.append(" * Subsystem: \n");
        buffer.append(" * \n");
        buffer.append(" * Name                 Date       Release\n");
        buffer.append(" * -------------------- ---------- ---------------------------------------\n");
        buffer.append(" * Kodiak               04-05-2007 6.0\n");
        buffer.append(" * \n");
        buffer.append(" * \n");
        buffer.append(" * #401, 4th Floor, 'Prestige Sigma'\n");
        buffer.append(" * No.3, Vittal Mallya Road \n");
        buffer.append(" * Bangalore - 560 001 \n");
        buffer.append(" * www.kodiaknetworks.com \n");
        buffer.append(" * All Rights Reserved.\n");
        buffer.append(" * \n");
        buffer.append(" * This software is the confidential and proprietary information of Kodiak \n");
        buffer.append(" * Networks, Inc. You shall not disclose such confidential information and\n");
        buffer.append(" * shall use it only in accordance with the terms of the license agreement\n");
        buffer.append(" * you entered into with Kodiak Networks. \n");
        buffer.append(" **************************************************************************/\n");
        buffer.append("// ----------------------------------------------------------------\n");
        buffer.append("// The following code has been auto-generated :\n");
        buffer.append("// ----------------------------------------------------------------\n");

        if (packageName != null) {
            buffer.append("package " + packageName + ";\n");
        }

        for (Iterator it = classConfigMap.values().iterator(); it.hasNext();) {
            buffer.append(((KnClassConfig)it.next()).getCodeAsString(CLASSINDENT, MEMINDENT)).append("\n");
        }

        String packageDir = packageName.replace('.', File.separatorChar);
        File dirFile = new File(outputDir + File.separatorChar + packageDir);
        dirFile.mkdirs();
        File genFile = new File(dirFile, fileName);
        if (genFile.exists()) {
           System.out.println("File already exists at " + genFile.getAbsolutePath() + ". deleting...");
           genFile.delete();
        }

        if (genFile.createNewFile()) {
            FileOutputStream out = new FileOutputStream(genFile);
            byte bytes[] = buffer.toString().getBytes();
            out.write(bytes);
            out.flush();
            out.close();
            System.out.println("File Created generated successfully at " + genFile.getAbsolutePath());
        }
    }
}
