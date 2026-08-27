/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnBulkFwFactory.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker P       21-sept-2012   7.4
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
 * ************************************************************************/
package com.kodiak.xdms.bulkfw.factory;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.bulkfw.factory.docdiff.KnSubsDocDiffBulkFactory;
import com.kodiak.xdms.bulkfw.factory.subsprov.KnSubsProvBulkOrderFactory;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;

public class KnBulkFwFactory {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkFwFactory.class);

    private static final String CLASS_NAME = KnBulkFwFactory.class.getName();

    protected KnBulkFwFactory() {

    }

    public static IBulkOrderFactory createBulkOrderFactory(KnBulkFwConstants.BULK_ORDER_TYPE bulkOrderType) {
        String methodName = "createBulkOrderFactory(BULK_ORDER_TYPE)";
        knLogger.debug(methodName, "Bulk Order Factory for type ", bulkOrderType.value());

        switch (bulkOrderType) {
            case SUBS_NOTIFICATION:
                return new KnSubsDocDiffBulkFactory();
            case SUBS_PAM_CREATE:
                return new KnSubsProvBulkOrderFactory();
            case SUBS_PAM_DELETE:
                return new KnSubsProvBulkOrderFactory();
            default:
                return new KnSubsProvBulkOrderFactory();
        }

    }

//    private static Object loadClass(String className) throws KnException {
//        String methodName = "loadClass";
//        Class classObj = null;
//        String subConfigClass = "com.kodiak.frameworks.bulkfw.factory.docdiff.KnSubsDocDiffBulkFactory";
//        if (className == null || className.trim().equals("")) {
//            knLogger.error( methodName, "Class Name is empty");
//            throw new KnException("Class name is empty/null", className);
//        }
//
//        knLogger.debug( methodName, "Loading class ", className);
//        try {
//            classObj = Class.forName(className);
//        } catch (ClassNotFoundException e) {
//            knLogger.error( methodName, "Class Not found - ", e.getMessage());
//            throw new KnException("Class Not Found", className);
//        }
//        knLogger.debug( methodName, "Class Loaded");
//
//        Object obj = null;
//        try {
//            obj = classObj.newInstance();
//        } catch (InstantiationException e) {
//            knLogger.error( methodName, "Exception Occured", e.getMessage());
//            knLogger.error( methodName, e);
//            throw new KnException("1001", e.getMessage());
//        } catch (IllegalAccessException e) {
//            knLogger.error( methodName, "Exception Occured", e.getMessage());
//            knLogger.error( methodName, e);
//            throw new KnException("1001", e.getMessage());
//        }
//        return obj;
//    }


}
